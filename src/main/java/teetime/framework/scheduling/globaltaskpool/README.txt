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

Q: Should front stages immediately be added to the queue, or should they be added not until the pool is empty?
A: TODO

Q: Should front stages always be added to the back of the queue?
A: Yes. If there are two front stages E and F with priorities of 4 and 5, respectively, then E gets a chance to be executed.

Q: How often should the successors of a stage be added to the pool?
P: 
	- once after multiple stage executions
	- once after single stage execution
	- once after sending to output port
	
	- to all successors
	- to the associated successor
A: TODO

[Concepts]
- front stages: variable set o stages which is added if the stage pool is empty
- paused stage:
	- if pipe is full
	=> By pausing the thread, we memorize the current instruction pointer.
	- if element was added to a pipe
	=> don't know why anymore
- schedule stage:
	- if element was added to a pipe
	=> to notify idle threads and to prioritize the target stage
	- if stage is being yielded
	=> to ensure awakening it eventually
- executing stage:
	- in thread.run
	=> to ensure critical section for this stage instance
	
[Idle thread fetches next stage from pool]
1. fetch from deepest level
	=> ensures back-pressure
2. fetch a stage instance which is not being executed in this moment
	=> avoid re-scheduling without progress (not that important)
