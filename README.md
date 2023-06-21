Run `bb dev` to get started. See `bb tasks` for other commands.

# Development

## App

Ensure `index.js` requires the development target.

Run `yarn repl` to start the ClojureScript compiler and Clojure REPL.
Run `yarn start` to start the Metro Dev server.

## Backend

Connect to the Clojure REPL started by `yarn repl`, and run

``` clojure
user=> (require 'dev)
user=> (dev/start)
```

This will start the development API server.

Update the app config to use the local endpoint (for now in `api.cljs`).

# Release

## Build a release version of the app

Ensure `index.js` requires the production target.
Ensure the app config (for now in `api.cljs`) points to production host.

Compile the production ClojureScript:


``` sh
yarn cljs:build
```

In XCode, run a build using the Bardistry Release scheme.

## Deploy backend

Deploy backend using:

```
git push prod main
```

Ensure it restarted using either:

``` sh
bb logs
```

Or log into the production server to check on the app service status:

```sh
ssh app@bardistry.app
systemctl status app
```
