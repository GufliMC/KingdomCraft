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
kingdom = kdc.createKingdom("gufland");

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
kdc.getEventManager().addListener(new EventListener() {
    @Override
    public void onKingdomJoin(User user) {
        System.out.printf("%s joined kingdom %s%n", user.getName(), user.getKingdom().getName());
    }

    @Override
    public void onKingdomLeave(User user, Kingdom oldKingdom) {
        System.out.printf("%s left kingdom %s%n", user.getName(), oldKingdom.getName());
    }

    @Override
    public void onKingdomCreate(Kingdom kingdom) {
        System.out.printf("Created kingdom %s%n", kingdom.getName());
    }

    @Override
    public void onKingdomDelete(Kingdom kingdom) {
        System.out.printf("Deleted kingdom %s%n", kingdom.getName());
    }

    @Override
    public void onRankChange(User user, Rank oldRank) {
        System.out.printf("Rank of %s changed from %s to %s%n", user.getName(), oldRank.getName(), user.getRank().getName());
    }
});
```

