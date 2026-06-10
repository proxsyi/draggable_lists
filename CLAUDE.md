# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Draggable Lists is a Minecraft client mod (using mixins) that adds drag-and-drop reordering to in-game lists: the resource pack/datapack selection screens and the multiplayer server list. Targets Fabric only, for Minecraft 26.1+.

## Build commands

- `./gradlew build` — build the mod jar
- `./gradlew clean` — clean build outputs
- `./gradlew :runClient` — run the game client for manual testing (if configured by Loom)

There is no test suite; this is a Minecraft client mod and changes are verified by running the game or by code inspection.

Targets Minecraft 26.1 (`gradle.properties`), Java 25, Gradle 9.4, using the `net.fabricmc.fabric-loom` plugin (no remapping — the mod compiles directly against official Mojang mappings, so `modImplementation`/`remapJar` are not used).

## Project structure

Single Gradle project (`src/main/java`, `src/main/resources`) — all gameplay logic, mixins, config, and assets live here.

- `com.mrmelon54.DraggableLists.fabric` — Fabric entry points: `DraggableListsFabric` (`ModInitializer`, calls `DraggableLists.init()`) and `ModMenuIntegration` (Mod Menu config screen registration).
- Everything else (`DragManager`, `DragList`, `DragItem`, `Cursor`, `config/`, `duck/`, `mixin/`) is shared core logic, described below.

## Core architecture

The drag-and-drop behavior is implemented once and wired into vanilla list screens via Mixin, so it applies uniformly to resource packs, datapacks, and the server list.

- **`DragManager<T, E>`** (`DragManager.java`) — the core drag controller. Handles mouse press/drag/release, computes drop position from mouse Y, renders the dragged item floating, handles soft auto-scroll while dragging near list edges, and shifts the rendering of other rows to show the gap/insertion point.
- **`DragList<T, E>`** — interface that any `ObjectSelectionList` subclass must implement (via mixin) to be drag-enabled. Exposes the geometry/state accessors (`draggable_lists$getRowTop`, `getScrollAmount`, `moveEntry`, etc.) that `DragManager` needs, all prefixed with `draggable_lists$` to avoid clashing with vanilla members.
- **`DragItem<T, E>`** — interface implemented by list entries (e.g. `OnlineServerEntry`, pack entries) exposing the underlying data object and drag state.
- **`Cursor`** — manages cursor icon changes while dragging.

### Mixins (`mixin/`)

- `mixin/server/` — `ServerSelectionListMixin` (implements `DragList`, delegates to a `DragManager`), `ServerListMixin`/`OnlineServerEntryMixin` (implement `DragList`/`DragItem` plumbing for the server list), `JoinMultiplayerScreenMixin`.
- `mixin/packs/` — equivalent mixins for `PackSelectionModel`, `PackSelectionModel.EntryBase`, `TransferableSelectionList` (and its `PackEntry`), and `PackSelectionScreen` for resource pack/datapack lists.
- `mixin/MinecraftMixin` — top-level hook for mod init.

### Duck interfaces (`duck/`)

`ServerListDuckProvider`, `AbstractPackDuckProvider`, `ResourcePackOrganizerDuckProvider` — narrow interfaces injected onto vanilla classes via mixin so the drag logic can reorder the underlying data lists/models without depending on mixin internals directly.

### Config

`ConfigStructure` (cloth-config + AutoConfig, serialized with Jankson) exposes:
- `disableResourcePackArrows` / `disableServerArrows` — hide vanilla up/down reorder arrows when dragging is enabled.
- `resourcePackDraggingEnabled` / `serverDraggingEnabled` — `EnabledMode` enum toggle per feature.

The config screen is registered through Mod Menu via `ModMenuIntegration`.

### Access widening

`src/main/resources/draggable_lists.accesswidener` makes `PackSelectionModel.EntryBase` accessible for mixin use.

## Versioning

`mod_version` in `gradle.properties` is the single source of truth for the mod version.
