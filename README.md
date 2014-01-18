#Scrabble
#### Final Product Downloads:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Macintosh](http://www.andrew.cmu.edu/user/areyes/files/Scrabble.dmg)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Windows](http://www.andrew.cmu.edu/user/areyes/files/scrabble.zip)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Linux (Comming Soon!)

**_Note:_** All downloads require a 64-bit operating system

#### Description:
A simple Java implementation of the original game with a small twist. With the special tiles mode, players can set traps for each other and spice up the gameplay! You can create your own trap tiles by editing the source code above or just use the default ones created. Click the "how to play" button on the in-game welcome screen or  [click here](https://github.com/a-rey/scrabble/blob/master/assets/manual.pdf) for more details on the default special tiles included.

To receive the completely packaged application, visit the links above. Enjoy!

#### Adding New Special Tiles:

To add new special tiles you need to create a new class in the _com.aaronmreyes.scrabble.core.tiles_ package that extends the _com.aaronmreyes.scrabble.core.tiles.AbstractTile_ class. Your class declaration should look like this:

```java
public class MyNewSpecialTile extends AbstractTile
```

Your constructor should call the super classes constructor like so:

```java
public MyNewSpecialTile(Location loc, int priority, color color) {
		super(loc, priority, color);
}
```

All you need to do then is just override the method _doAbility(List<Player> players, int score)_. This method is called when your tile is activated. Special tiles can be bad or good and can affect any player (along with their score) and the current move's score. 

The last thing you need to do is add your new special tile to the _com.aaronmreyes.scrabble.core.Board.setUpSpecialTiles(List<AbstractTile> letterBank)_ method case statement and add one to the **NUM_SPECIAL_TILES** variable at the top of the Board class declaration for each new special tile you make.

#### Adding New Ability Tiles:

To add new ability tiles you need to create a new class in the _com.aaronmreyes.scrabble.core.tiles_ package that extends the _com.aaronmreyes.scrabble.core.tiles.AbilityTile_ class. Your class declaration should look like this:

```java
public class MyNewAbilityTile extends AbilityTile
```

Your constructor should call the super classes constructor like so:

```java
public MyNewAbilityTile(Location loc, int priority, color color) {
		super(loc, priority, color);
}
```

All you need to do then is just override the method _doAbility(List<Player> players, int score)_. This method is called when your tile is activated. Ability tiles can be bad or good and can affect any player (along with their score) and the current move's score. Examples of ability tiles already implemented in the packaged release are double letter tiles and triple word tiles.

The last thing you need to do is add your new ability tile to the _com.aaronmreyes.scrabble.core.Board.setUpAbilityTiles()_ method case statement. This method works by reading the file /assets/ability.txt and using the acronyms in there to figure out which ability tile to place and where to place it on the board. See /assets/README.txt for what the current acronyms mean. What you need to do is add your unique acronym to the /assets/ability.txt and then add a new case statement in the _setUpAbilityTiles()_ method with your unique acronym as the case. Then create an instance of your ability tile in the case statement and assign it a priority and a color. This color will be used to identify your ability tile on the board and the priority will be used to figure out when your ability tile should be used when calculating a move score. For example, a low priority ability tile would be a double/triple word tile because its ability needs to be applied to the total word score. A high priority tile would be a double/triple letter tile because its ability needs to be applied before later tiles (like a double/triple word).

**NOTE:** The highest priority is 0 and the lowest priority is 10. Defined by **HIGH_PRIORITY** and **LOW_PRIORITY** in _com.aaronmreyes.scrabble.core.Board_.

<p align="center"><img src="http://i00.i.aliimg.com/img/pb/704/989/105/105989704_388.jpg" alt=""/></p>

_image_ _borrowed_ _from_ [here](http://www.alibaba.com/product-detail/wooden-scrabble-tiles-for-jewelry_137115928.html)
