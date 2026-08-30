# FrameForward design context

Android-first, Simplified Chinese photography coaching. The shell is a calm camera viewfinder: practical, focused, and confident.

- Canvas: `#F5F9F8`
- Ink: `#102A43`
- Focus-frame accent: `#18A999`
- Supporting text: `#52616B`
- Surface: `#FFFFFF`

Use the accent for selection and forward progress, never as generic decoration. Runtime ownership is in `frame-forward-app/app/AppShell.tsx` and `frame-forward-app/shared/components/ScreenState.tsx`; future token changes must update both together.
