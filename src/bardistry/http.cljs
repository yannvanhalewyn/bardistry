(ns bardistry.http
  (:require [re-frame.core :as rf :refer [reg-sub reg-event-db reg-event-fx]]
            [cljs-fetch.core :as fetch]
            [reagent.ratom :as ratom]
            [cljs.core.async :refer [<! go]]
            [medley.core :as m]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Request status

(defn- mark-pending [db query-id]
  (assoc-in db [:db/app-state ::requests query-id] {::status ::pending}))

(defn- mark-success [db query-id]
  (assoc-in db [:db/app-state ::requests query-id] {::status ::success}))

(defn- mark-failed [db query-id body]
  (assoc-in db [:db/app-state ::requests query-id] {::status ::failure ::body body}))

(defn pending? [{::keys [:bardistry.http/status]}]
  (= status ::pending))

(defn failed? [{::keys [:bardistry.http/status]}]
  (= status ::failure))

(defn success? [{::keys [:bardistry.http/status]}]
  (= status ::success))

(defn loading?
  "A loading status is either a status that hasn't fired yet (e.g. status is
  nil) or one that is still pending. Since subscribing to a status can briefly
  return a nil status if the http request hasn't fired yet, this is the
  recommended way to check for loading status"
  [{::keys [:bardistry.http/status] :as request-status}]
  (or (nil? status) (pending? request-status)))

(defn- get-status [db query-id]
  (get-in db [:db/app-state ::requests query-id]))

(defn- clear-status [db query-id]
  (m/dissoc-in db [:db/app-state ::requests query-id]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Events and FX

(rf/reg-fx
 ::http
 (fn [{::keys [:bardistry.http/url :bardistry.http/on-success :bardistry.http/on-failure]}]
   (go
     (let [[res err] (<! (fetch/dev-skip-mocks! #(fetch/GET url)))]
       (cond
         err                      (rf/dispatch (conj on-failure err))
         (not (:response/ok res)) (rf/dispatch (conj on-failure res))
         :else                    (rf/dispatch (conj on-success res)))))))

(reg-event-fx
 ::read
 (fn [{:keys [db]} [_ query-id {::keys [:bardistry.http/url] :as config}]]
   {:db (mark-pending db query-id)
    ::http {::url url
            ::on-success [::success query-id config]
            ::on-failure [::failure query-id config]}}))

(reg-event-fx
 ::success
 (fn [{:keys [db]} [_ query-id config response]]
   {:db (-> db
            (mark-success query-id)
            (assoc-in (:db/path config) (:response/body response)))
    :fx [(when-let [on-success (::on-success config)]
           [:dispatch (conj on-success response)])]}))

(reg-event-fx
 ::failure
 (fn [{:keys [db]} [_ query-id config response]]
   {:db (mark-failed db query-id (:response/body response))
    :fx [(when-let [on-failure (::on-failure config)]
           [:dispatch (conj on-failure response)])]}))

(reg-event-db
 ::dispose
 (fn [db [_ query-id {:keys [:db/path]}]]
   (-> db
       (m/dissoc-in path)
       (clear-status query-id))))

(defn reg-sub-remote
  "Registers a Re-Frame subscription that, when consumed, will fetch data from
  the configured `::http/url` endpoint and store it at `:db/path`. The value of
  the subscription is always a tuple of `[response-data request-status]`

  ```clojure
(http/reg-sub-remote
 ::query-id
 (fn [[_ article]]
   {::http/url (article/content-url article)
    ::http/dispose? true
    :db/path [:db/app-state :app-state/article-content url]}))
  ```
  "
  [query-id config-fn]
  (rf/reg-sub-raw
   query-id
   (fn [db sub-vec]
     (let [config (config-fn sub-vec)
           reaction (ratom/make-reaction
                     (fn []
                       [(get-in @db (:db/path config))
                        (get-status @db sub-vec)])
                     :on-dispose
                     (when (::dispose? config)
                       #(rf/dispatch [::dispose query-id config])))]
       (when (and (nil? (first @reaction))
                  (or (not (contains? config ::fire?))
                      (::fire? config)))
         (rf/dispatch [::read sub-vec config]))
       reaction))))

(reg-sub
 ::status
 (fn [db [_ query-id]]
   (get-status db query-id)))
