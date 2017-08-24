[Example configuration]

	  ->C->D-
A->B-|		 |->G->H
	  ->E->F-

[Priorities (low=small number, high=large number) for back-pressure]
A:0
B:1
C:2
D:3
E:2
F:3
G:4
H:5

[Questions]
Q: Should each thread have a private queue in addition to the global queue?
A: TODO

