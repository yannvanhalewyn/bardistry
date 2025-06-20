# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bardistry is a singer songwriter toolkit. It's a React Native mobile application meant for managing song lyrics etc.., built with ClojureScript frontend and Clojure backend. It has features like managing song lyrics, creating setlists, performing, tagging and editing song metadata, organising songs. Additionally it also helps with practicing, like offering a voice recorder wich can record some practice sessions and track progression of the song etc.. It also includes a simple tuner, which microtonal adjustment, and a flexible good sounding metronome.

The whole thing is made for live performance, and iPads, so it's crucial for the
app to work in offline mode, and to keep all the data locally. Technically this
is achieved via keeping track of events locally and synchronizing them to the
server when it comes online. There is message merging and de-duplication on the
backend.

## Tech Stack

### Frontend (React Native + ClojureScript)
- **React Native 0.71.8** - Mobile app framework
- **ClojureScript** - Compiled to JavaScript via Shadow CLJS
- **Reagent** - React wrapper for ClojureScript
- **Re-frame** - State management
- **NativeWind** - Tailwind CSS for React Native styling
- **React Navigation** - Navigation management
- **AsyncStorage** - Local data persistence

### Backend (Clojure)
- **Clojure 1.11.1** - Server-side language
- **Biff Framework** - Full-stack web framework
- **XTDB** - Database for persistence
- **Jetty** - HTTP server
- **Malli** - Schema validation

### Build Tools
- **Shadow CLJS** - ClojureScript compilation
- **Babashka** - Task runner
- **Metro** - React Native bundler
- **Yarn** - Package management

## Project Structure

```
src/bardistry/
├── core.clj           # Backend entry point with Biff system setup
├── core.cljs          # Frontend entry point with React Native app
├── App.js             # Main React Native navigation component
├── api.cljs           # Frontend API client
├── db.cljs            # Frontend state management
├── navigation.cljs    # Navigation utilities
├── server/
│   ├── api.clj        # Backend API routes and schema
│   ├── middleware.clj # HTTP middleware
│   └── worker.clj     # Background jobs
└── songlist/
    ├── songlist.cljs  # Main songlist component logic
    ├── db.cljs        # Songlist-specific data access
    ├── lyrics.cljs    # Lyrics viewing/editing logic
    ├── tx.cljc        # Transaction functions (shared)
    └── *.js           # React Native UI components
```

## Development Commands

### Start Development Environment
```bash
# Start ClojureScript compiler and Clojure REPL
yarn repl

# Start Metro development server
yarn start

# Start backend server (in REPL)
(require 'dev)
(dev/start)

# Start full development environment
bb dev
```

### Build and Deploy
```bash
# Build production ClojureScript
yarn cljs:build

# View available Babashka tasks
bb tasks

# Deploy to production
bb deploy

# View production logs
bb logs
```

### Testing and Linting
```bash
# Run tests
yarn test

# Run ESLint
yarn lint

# Format Clojure code
bb format
```

## Architecture Patterns

### Data Flow
- **Frontend State**: Managed by Reagent atoms in `bardistry.db`
- **Backend Persistence**: XTDB database with transaction functions
- **API Communication**: REST endpoints at `/api/q` (queries) and `/api/mutate` (mutations)
- **Local Storage**: AsyncStorage for offline caching and navigation state

### Component Architecture
- **Hybrid Pattern**: ClojureScript logic components wrapping JavaScript UI components
- **Adapters**: `r/adapt-react-class` bridges React components to Reagent
- **State Passing**: Props passed from ClojureScript to JavaScript components

### Database Schema
Core entities defined in `bardistry.server.api/schema`:
- **Song**: `:song/id`, `:song/title`, `:song/artist`, `:song/tags`, `:song/lyrics`
- **Lyrics**: `:lyrics/arrangement`, `:lyrics/sections`
- **Section**: `:section/id`, `:section/title`, `:section/lines`, `:section/highlight?`

### Transaction System
- **Frontend**: Optimistic updates via `bardistry.songlist.tx/apply-mutations`
- **Backend**: Transaction functions in `bardistry.songlist.tx/tx-fns`
- **Sync**: Mutations sent to `/api/mutate` endpoint

## Configuration

### Environment Setup
- Development config in `config.edn`
- Secrets managed in `secrets.edn` (not committed)
- React Native configuration in `index.js` (switch between dev/prod targets)

### API Endpoints
- Backend runs on configurable port (default development setup)
- Frontend API client in `bardistry.api` handles request/response formatting
- Production deployment uses git-based deployment to `bardistry.app`

## Key Development Notes

- **Build Targets**: Switch `index.js` import between `target/app/dev` and `target/app/prod`
- **REPL Development**: Backend server started from REPL for interactive development
- **Navigation State**: Automatically persisted to AsyncStorage for better UX
- **Error Handling**: React Error Boundaries with reset capability
- **Offline Support**: Songs cached locally, optimistic updates for better UX
