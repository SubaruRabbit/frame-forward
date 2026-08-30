## 1. Course content

- [x] 1.1 `contracts`: define course catalog, versioned lesson, progress, assignment and feedback operations and verify OpenAPI validation passes.
- [x] 1.2 `frame-forward-server/course`: persist immutable content versions and user progress and verify regeneration does not overwrite a started version.
- [x] 1.3 `frame-forward-server/course`: add the P0 course structure and verify required basics, mirrorless, equipment and model categories are represented.
- [x] 1.4 `frame-forward-server/ai-workflow`: implement `qwen3.7-plus` structured course generation and verify schema, duplicate and terminology checks reject bad fixtures.
- [x] 1.5 `frame-forward-server/course`: inject versioned equipment material and uncertainty disclaimer rules and verify an unverified menu path is never stated as fact.
- [x] 1.6 `frame-forward-server/course`: implement assignment submission and lesson-focused AI feedback and verify progress updates after a valid JPEG assignment.

## 2. Learning UI

- [x] 2.1 `frame-forward-app/features/learning`: add course list, lesson, exercise and progress screens and verify a cached P0 lesson opens without new generation.
