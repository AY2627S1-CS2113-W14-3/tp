# CLI regression test

`input.txt` contains a complete add/modify/retrieve session, including invalid commands followed by valid ones.
`EXPECTED.txt` records the expected console output. The first two commands prepare the two-serving recipe used
in the Modify User Guide example.

Run `./gradlew check` (or `.\gradlew.bat check` on Windows). `ModifyWorkflowTest` launches the application with
assertions enabled, feeds it `input.txt`, and compares all output with `EXPECTED.txt`. This also runs in the
existing GitHub CI workflow. Add new cases to both fixtures when extending this session.

To demonstrate the feature interactively, run `./gradlew run --console=plain`
(or `.\gradlew.bat run --console=plain` on Windows) and enter the commands from `input.txt`.
