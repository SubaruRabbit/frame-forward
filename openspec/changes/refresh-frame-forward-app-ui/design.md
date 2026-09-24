## Context

See [proposal.md](proposal.md). `frame-forward-app` is an Android-first React Native client whose authenticated `AppShell` mounts the four existing feature destinations. The existing theme provides only four colour and four spacing values; the shell applies several hard-coded visual values. The approved product baseline requires Simplified Chinese, four top-level entries, Android safe-area support and recoverable visible states.

## Goals / Non-Goals

**Goals:**

- Translate the Canva FrameForward direction into a durable mobile design token layer and an Android-first shell.
- Reproduce the approved Canva home composition rather than a generic route header: `LIGHT JOURNAL`, a notification affordance, the 今日练习 hero, photography inspiration cards, quick learning, recent score and four-item navigation.
- Preserve public feature contracts while making taps, startup feedback and workflow notices accessible.

**Non-Goals:**

- No change to navigation state ownership, authentication, backend/API contracts, feature workflows, or user data.
- No new icon, animation or UI-library dependency; do not introduce a separate design-system package.

## Decisions

1. **Evolve the existing theme tokens rather than place visual constants in route screens.** Add semantic surface, text, border, action, spacing, radius and elevation values in `src/theme/tokens.ts`; share them between the app shell and state presentation. This makes the UI direction auditable and avoids per-screen divergence. Replacing the current theme module was rejected because it would enlarge a visual refactor into an architectural migration.

2. **Keep `AppShell` as the interaction owner and use small presentational components only where repeated.** `AppShell` continues to own authentication, route persistence and workflow notices. A shell header, navigation item and notice surface can be presentation-only components, receiving data and callbacks from the shell. Moving state into global context was rejected because these values are local to the authenticated shell.

3. **Use text-led navigation with tokenised vector-like glyphs only when native-safe, never emoji.** Each of the four entries retains its Chinese label and role/state semantics; active state uses an indicator plus weight and colour. This meets recognisability and avoids adding an icon package for a cosmetic change.

4. **Use the Canva canvas as the visual source of truth.** The approved page is the Chinese `LIGHT JOURNAL` photography-coach home: a dark, image-led 今日练习 hero, compact horizontal inspiration cards, a concise tutorial row, a scored recent-shot row, and 首页 / 教程 / 图库 / 评分 navigation. The home presentation may wrap, but must not replace, the existing photo-import, scene-analysis and shooting-plan feature entry points.

5. **Keep photography imagery local and decorative.** Scenic images support orientation and card hierarchy only; they do not carry required instructions. Store them in the application assets and preserve descriptive accessibility labels. Runtime UI code must not load third-party image URLs.

## Risks / Trade-offs

- [Canva design has no exportable source layers] → The browser-inspected canvas is the visual reference. Use its visible Chinese hierarchy and accessibility names; do not invent or alter business behavior.
- [Feature screens own some current visual markup] → Limit this change to tokens and shared shell/state treatment; follow-up changes can migrate feature-level layouts incrementally.
- [Large system font settings increase navigation/content height] → Enable font scaling, use adaptable layout sizing and test the shell at the largest supported text size.

## Migration Plan

1. Add and consume semantic tokens and shell-level presentational styles.
2. Preserve current routes and public feature props; update shell and component tests for the new accessible states.
3. Verify lint, type checks, architecture checks, unit tests and Android bundle/build checks. Roll back by reverting the UI-only app change; stored route and server data remain compatible.
