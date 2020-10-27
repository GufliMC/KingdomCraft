# KingdomCraft
The #1 minecraft kingdom plugin for easily managing your kingdoms!

**__Before using the api be sure to add "KingdomCraft" to your dependencies!__**

##### Retrieving the **KingdomCraftApi** Object:
```java
KingdomCraftApi api = KingdomCraft.getApi();
```

##### Retrieving the **KingdomCraft** plugin:
```java
KingdomCraft plugin = KingdomCraft.getPlugin();
```

##### Using the api to retrieve data from an online user:
```java
KingdomUser user = api.getUser(Player);
KingdomUser user = api.getUser("iGufGuf");
```
> Retrieving a user with the username will also return if the user is not online


##### Using the api to retrieve data from an offline user:
```java
KingdomUser user = api.getOfflineUser(UUID);
```
> After modifying offline userdata make sure to save the data!


##### Saving a user object:
```java
plugin.save(user);
```

##### Using the api to retrieve data from a kingdom:
```java
KingdomObject kd = api.getKingdom("kingdomname");
```

##### Saving the kingdom object:
```java
plugin.save(kd);
```

##### Manipulating data of kingdom objects:
KingdomUser, KingdomObject & KingdomRank all extends on KingdomData
```java
if ( object.hasData(key) ) {
    if ( object.getData(key) != value ) {
        object.setData(key, value);
    }
}
```
> KingdomUser has also the possibility to use hasLocalData, getLocalData & setLocalData, this data will not be written to the file

##### Getting the relation of a kingdom:
```java
KingdomRelation rel = api.getRelation(kingdom, targetkingdom);
```

##### Retrieving a kingdomrank from a kingdom
```java
KingdomRank rank = kd.getRank("rankname");
```

##### Changing the rank/kingdom of a user:
```java
api.setRank(user, rank);
api.setKingdom(user, kingdom);
```

KingdomCraft Custom events
---

### KingdomChatEvent
Called when a player sends a message that is manipulated with kingdomcraft

**Methods**: 
getPlayer, getFormat, setFormat, getMessage, setMessage, getReceivers, isCancelled, setCancelled

### KingdomJoinEvent
Called when a player joins a kingdom

**Methods**: 
getUser
> user.getKingdom() & user.getRank() will return the new kingdom and rank.

### KingdomLeaveEvent
Called when a player leaves a kingdom

**Methods**: 
getUser, getOldRank, getOldKingdom
