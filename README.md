# KingdomCraft Starter

![KingdomCraft Logo](https://repository-images.githubusercontent.com/89062719/1c4d5500-6283-11eb-80c4-6806320076e9)

Go back to the medieval ages with KingdomCraft. Create a kingdom, make ranks, defend your land or invade the enemies. Try this new roleplay focussed version of factions right now!

* Create and manage unlimited kingdoms
* Max members on kingdoms and ranks
* Custom permissions for each rank
* Invite players for your kingdom
* MySQL, Postgress and H2 database support
* Online webeditor
* Custom chat channels per kingdom
* Allies, enemies, neutral and truce
* Custom join, quit, death and kill messages
* Much more...

Go to https://kingdomcraft.be for more information. <br/>
Check our [discord](https://discord.gg/UFEcurxWsV) for support and updates!

## Development guide

### Gradle

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.jorisguffens:KingdomCraft:-SNAPSHOT'
}
```

### Maven

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.jorisguffens</groupId>
    <artifactId>KingdomCraft</artifactId>
    <version>-SNAPSHOT</version>
</dependency>
```

### API Usage

#### Kingdoms & users
```
// Get the KingdomCraft instance
KingdomCraft kdc = KingdomCraftProvider.get();

// Get an online user by name or uuid
User onlineUser = kdc.getOnlineUser("iGufGuf");
System.out.printf("The kingdom of %s is %s%n", onlineUser.getName(), onlineUser.getKingdom().getName());

// Get an offline user by name or uuid
kdc.getUser("iGufGuf").thenAccept((user -> {
    System.out.printf("The rank of %s is %s%n", user.getName(), user.getRank().getName());
}));

// Get all kingdoms
kdc.getKingdoms().forEach(kingdom -> System.out.println(kingdom.getName()));

// Get kingdom by id
Kingdom kingdom = kdc.getKingdom(1);
System.out.printf("Amount of members in kingdom %s: %o%n", kingdom.getName(), kingdom.getMemberCount());

// Get kingdom by name
kingdom = kdc.getKingdom("gufnation");
System.out.printf("The defaultrank of kingdom %s is %s%n", kingdom.getName(), kingdom.getDefaultRank().getName());

// Create a kingdom
kdc.createKingdom("gufland").thenAccept((kingdom) -> {
    System.out.printf("Created kingdom: %s", kingdom.getName());
});

// Add a rank
Rank rank = kingdom.createRank("King");
System.out.printf("Amount of members in rank %s: %o%n", rank.getName(), rank.getMemberCount());

rank.setLevel(10);

// ALWAYS SAVE ASYNC
kdc.saveAsync(rank);

// Get relations of a kingdom
kdc.getRelations(kingdom).forEach(rel -> System.out.println(rel.getType()));
```

#### Events
```
kdc.getEventManager().addListener(PlayerLoadedEvent.class, (event) -> {
    System.out.printf("The data of %s has been loaded.%n", event.getPlayer().getName());
});

kdc.getEventManager().addListener(UserJoinKingdomEvent.class, (event) -> {
    User user = event.getUser();
    System.out.printf("%s joined kingdom %s%n", user.getName(), user.getKingdom().getName());
});

kdc.getEventManager().addListener(UserLeaveKingdomEvent.class, (event) -> {
    User user = event.getUser();
    System.out.printf("%s left kingdom %s%n", user.getName(), oldKingdom.getName());
});

kdc.getEventManager().addListener(UserRankChangeEvent.class, (event) -> {
    User user = event.getUser();
    System.out.printf("Rank of %s changed from %s to %s%n", 
            user.getName(), event.getPreviousRank().getName(), user.getRank().getName());
});

kdc.getEventManager().addListener(KingdomDeleteEvent.class, (event) -> {
    System.out.printf("Deleted kingdom %s%n", event.getKingdom().getName());
});

kdc.getEventManager().addListener(KingdomCreateEvent.class, (event) -> {
    System.out.printf("Created kingdom %s%n", event.getKingdom().getName());
});
```

