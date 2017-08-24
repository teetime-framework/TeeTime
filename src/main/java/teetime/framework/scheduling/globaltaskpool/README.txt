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

Q: Should we switch the thread upon receiving null on an input port?
A: No. If a stage requires a non-null element from one of multiple input ports, it must iterate over all input ports and skip all input ports with a null element. 
	Hence, skipping is only possible if we do not switch the thread upon the first null element.

