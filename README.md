# Backgammon
Game of Backgammon implemented in Java.




## Project overview
Code is inside `src`.  Run `src/Backgammon.java` for the game. Seperate packages are divided into folders inside `src`.

| Folder        | Contains           |
| ------------- |:-------------:|
| `src/ai` | AI for computer. | 
| `src/gui` | Graphics user interface |
| `src/leader` | Manages every session | 
| `src/rules` | Rules for Bacgammon game | 
| `src/utils` | Utils | 

![Backgammon in action](https://github.com/urhprimozic/Backgammon/blob/main/thumb.png)

### Computer AI
Computer player uses Monte Carlo Tree Search with intuitive hevristic. Heavily inspired by the work at [alpha zero general](https://github.com/suragnair/alpha-zero-general).

## Usage 
To play a new game, select player type from the **New game** menu. To change the difficulty of computer player, click **AI dificulty**.

To make a move, first click **ROLL DICE**, than click and drag desired movable chip to a desired location. Off board chips are stored on the right side, removed chips are on the wood in the middle.

![Human player playing vs computer](https://github.com/urhprimozic/Backgammon/blob/main/demo.gif)

