# java-chess
Chess game in Java

## it's a game

It's my very first 'big project', i neved touched JPanel and JFrame before, or made any GUI programs.

I decided to a simple app to train and i chose chess. (i deeply regret it was way harder than i thought)
The code for the GUI is really messy, and the code for generating moves is even more chaotic.

So expect some bugs when playing

## installation

Note that you need java to be installed on your machine.

1. Clone the repository : 
```
git clone https://github.com/nanthasoru/java-chess.git
```

2. To run the program, make sure to be in the java-chess/src/ directory:
```
cd java-chess/src/
```

3. Build .class files with :
```
javac */*.java
```

4. Run the program :
```
java main/Main
```

5. Start the GUI
```
build
```

6. Hit enter and there you go

## fair warning

To test if i successfully implemented the game i watched some videos on chess programming (like Sebastian Lague's coding adventure), and did performance tests.
As far as i know on position[1] : r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1
I get 4085662 nodes instead of 4085603

The problem comes from the pawn on d5

My output :
d5d6: 79604
d5e6: 97470

Stockfish output :
d5d6: 79551
d5e6: 97464

So expect some bugs when playing