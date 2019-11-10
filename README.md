# Sheriff of Nottingham

Simple simulation of Sheriff of Nottingham Game for 
Object Oriented Programming Homework.

## Design Pattern

I made a class for every type of player(Basic, Greedy and Bribed).
For their instantiation I used Factory Pattern.
Every player has a hand, a bag and a stand. 
The stand uses a frequency array because a list can grow a lot.
The others uses array lists because we need the original cards' order
(and they have fixed capacity).
The main logic is implemented in the Game Class, which runs the game, 
apply bonuses and prints the standings.
Also, all the 
[SOLID](https://itnext.io/solid-principles-explanation-and-examples-715b975dcad4) 
principles are respected.

### UML Diagram
![Imgur](https://i.imgur.com/Y8NIg3r.png)

## Implementation

The code speaks for itself. I just followed the requirement. 
For creating the bag I selected the cards according to the strategies.
At the end of the match I added bonuses for illegal goods. 
After I added the profits of goods to the coins sum of every players.
Then for every good I rewarded the best 2 players.


## Coding Style

 I followed [google](https://google.github.io/styleguide/javaguide.html) coding style.


## Author

[Rica Radu-Leonard 325CA](https://github.com/raduleo19)

