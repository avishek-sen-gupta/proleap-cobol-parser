000100 IDENTIFICATION DIVISION.
000200 PROGRAM-ID. QUALIFIED.
000300 PROCEDURE DIVISION.
000400     COPY QualifiedBook
000500          REPLACING FALSE-DATA-1 BY TRUE-Q-04 OF TRUE-Q-03
000600                                       IN TRUE-Q-02
000700                    FALSE-DATA-2 BY Z (2, 1, 1)
000800                    FALSE-DATA-3
000900                       OF GRP BY TRUE-3
001000                    FALSE-DATA-4 BY Z
001100                       (1, 2, 1).
