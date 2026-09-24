# FrameForward design context

Android-first, Simplified Chinese photography coaching. The shell is a calm camera viewfinder: practical, focused, and confident.

- Canvas: `#F5F9F8`
- Ink: `#102A43`
- Focus-frame accent: `#18A999`
- Supporting text: `#52616B`
- Surface: `#FFFFFF`

The runtime token owner is `frame-forward-app/src/theme/tokens.ts`; it extends these foundation colours with semantic canvas, surface, border, divider, pressed-accent, danger and warning roles. Shared mobile components consume those semantic tokens rather than declaring colour, radius or spacing literals.

The shell uses an 4/8-point rhythm (`4, 8, 12, 16, 24, 32, 40`), 10/14/20-point radii, and restrained Android elevation only for cards and persistent navigation. The active destination pairs the focus colour with a selected surface and heavier label, so selection is not expressed by colour alone.

Use the accent for selection and forward progress, never as generic decoration. The app shell (`frame-forward-app/src/app/AppShell.tsx`) and shared states (`frame-forward-app/src/components/ScreenState.tsx`) consume the canonical runtime tokens; future token changes must update both together.
