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
Game menus are in Slovene. To play a new game, select player type from the **Nova igra** menu. To change the difficulty of computer player, click **Težavnost**.

To make a move, first click **ROLL DICE**, than click and drag a desired chip to a desired location. 

