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

Q: When to self-schedule?
A: 
	- if the stage is a producer and not yet terminated (with no input ports)
	- if the input ports of the stage still contain elements and not yet terminated 
	[- if the stage is a front stage and not yet terminated (with at least one non-empty input port)]

Q: Should front stages always be added to the back of the queue?
A: Yes. If there are two front stages E and F with priorities of 4 and 5, respectively, then E gets a chance to be executed.

Q: How often should the successors of a stage be added to the pool?
P: 
	- once after multiple stage executions (how to measure?: exploit push index of each output port)
	- once after single stage execution (how to measure?: exploit push index of each output port)
	- once after sending to output port

	- to all successors without measurement
	- to the associated successor (how to measure?: exploit push index of each output port)
	
	- always schedule
	- schedule if not already in task pool
A: TODO

Q: What if a producer (or a front stage in general?) produces multiple elements in one execution?
P: 
	- pause thread after producing x elements
		=> avoids blocking on full pipe
		=> reduces number of idle threads
	- on full pipe, schedule the target stage in parallel
		=> deadlock if only one thread is used
	- after each execution, let the thread decide on when to pull a new stage from the queue (e.g., if one execution produces at least x elements)
A: TODO

Q: What is the optimal size of the pipe?
A:	Minimum: number of executions per task so that the thread does not block due size limitation
	=> problem: one execution can always produce more than one element.
	Maximum: TODO (unclear)

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

[Schedule queue]
Stages are ordered according to their level index (only).

-scheduleStage(s):
	-insert stage if not already added (Set<> behavior)
	-synchronized
	-

-removeNextStage():
	-remove an arbitrary stage with the deepest level
	-synchronized
	-deepest level may not necessarily be the key; an (ordered) queue would be sufficient
	-reserve a slot at index i for stage with index i
	=> order would always prioritize some stages within the same level (should not be a problem)
	-return stage at index i if not null, otherwise repeat with i-1
	-potential (cache) improvement (?): deepest stage is at index 0, lowest stage at index n-1
