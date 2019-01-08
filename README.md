# KingdomCraft
The #1 minecraft kingdom plugin for easily managing your kingdoms!

Download KingdomCraft **5.0.1** here: https://igufguf.com/project/1

*Before using the api be sure to add "KingdomCraft" to your dependencies!*

##### Retrieving the **KingdomCraft** plugin:
```java
KingdomCraft kdc = (KingdomCraft) Bukkit.getPluginManager().getPlugin("KingdomCraft");
```

##### Retrieving the **KingdomCraftApi** Object:
```java
KingdomCraftApi api = kdc.getApi();
```

##### Using the api to retrieve data from an online user:
```java
KingdomUser user = api.getUserHandler().getUser(Player);
KingdomUser user = api.getUserHandler().getUser("iGufGuf");
```


##### Using the api to retrieve data from an offline user:
```java
KingdomUser user = api.getUserHandler().getOfflineUser(UUID);
```
> After modifying offline userdata make sure to save the data!


##### Saving a user object:
```java
api.getUserHandler().save(user);
```

##### Using the api to retrieve data from a kingdom:
```java
Kingdom kd = api.getKingdomHandler().getKingdom("kingdomname");
```

##### Saving the kingdom data:
```java
api.getKingdomHandler().save(kd);
```

##### Manipulating custom data of a kingdom
KingdomUser, KingdomObject & KingdomRank all extends on KingdomData
```java
Kingdom kd = api.getKingdomHandler().getKingdom("kingdomname");
Storable kingdomdata = kd.getKingdomData();

kingdomdata.getData("key");
kingdomdata.setData("key", value);
```
> KingdomUser has also the possibility to use hasMemory, getMemory & setMemory, this data will not be written to the data file

##### Getting the relation of a kingdom:
```java
KingdomRelation rel = api.getRelationHandler().getRelation(kingdom, targetkingdom);
```

##### Retrieving a kingdomrank from a kingdom
```java
KingdomRank rank = kd.getRank("rankname");
```

##### Changing the rank/kingdom of a user:
```java
api.getUserHandler().setRank(user, rank);
api.getUserHandler().setKingdom(user, kingdom);
```

##### Kingdom flags

KingdomFlags are custom options you can change for every kingdom, ex.: friendlyfire, invite-only

The KingdomFlags can have different values like integers, booleans, strings... etc. 
For this, it makes use of java generics for easier programming. 
The KingdomFlag class is extendable for more variation in possible value types.

Register new flags
```java
public static final KingdomFlag<Boolean> INVITE_ONLY = new KingdomFlag<>("invite-only", Boolean.class);

FlagHandler flagHandler = api.getFlagHandler();
flagHandler.register(INVITE_ONLY);
```

It is recommend to use the flags by its direct reference like `KingdomFlag.INVITE_ONLY`.
If this is not possible, you can use following method.
```java
KingdomFlag flag = flagHandler.getFlag("friendlyfire");
```

Retrieve all flags for a kingdom
```java
List<KingdomFlag> flags = flagHandler.getFlags(kd);
```

Changing or retrieving flag values for a kingdom
```java
flagHandler.setFlag(kd, flag, value);
flagHandler.getFlag(kd, flag);
```

KingdomCraft Custom events
---

#### KingdomChatEvent
Called when a player sends a message that is manipulated with kingdomcraft

**Methods**: 
getPlayer, getFormat, setFormat(String), getMessage, setMessage(String), getReceivers, isCancelled, setCancelled(boolean)

#### KingdomJoinEvent
Called when a player joins a kingdom

**Methods**: 
getUser
> user.getKingdom() & user.getRank() will return the new kingdom and rank.

#### KingdomLeaveEvent
Called when a player leaves a kingdom

**Methods**: 
getUser, getOldRank, getOldKingdom

#### KingdomPlayerAttackEvent
Called when two players damage eachother

**Methods**:
getDamager, getTarget

Also contains the same methods from EntityDamageEvent

#### Other events
* KingdomPreLoadEvent
* KingdomLoadEvent
* KingdomSaveEvent
* KingdomUserLoadEvent
* KingdomUserSaveEvent
