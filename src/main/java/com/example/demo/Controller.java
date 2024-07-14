package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller implements Serializable {

    private static final String HEROES_FILE_PATH = "/app/data/heros.json";
    private static final String ACCOUNTS_FILE_PATH = "/app/data/acc.json";
    private static final String CARDS_FILE_PATH = "/app/data/cards.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public record Hero(int id, String name, int HP, int Damage, String type, String extra) {}
    public static List<Hero> heroes = new ArrayList<>();
    public static AtomicInteger counter = new AtomicInteger(1);

    private final Map<String, String> cardDesigns = new HashMap<>();
    public record Account(String username, String password, int coins) {}
    private Map<String, Account> accounts = new HashMap<>();


    private static final List<String> heroNames = List.of(
            "Ignis Flammenherz",
            "Pyra Glutsturm",
            "Vulcan Feuerschild",
            "Inferno Feuerschlag",
            "Helios Sonnentänzer",

            "Aqualis Meeresrufer",
            "Nerina Wellenweber",
            "Aquara Wasserschild",
            "Hydra Aquadonner",
            "Poseidon Meereswoge",

            "Flora Rankengriff",
            "Sylva Blattwerk",
            "Gaia Grünhüter",
            "Verdura Blattsturm",
            "Arborea Waldwisperer",

            "Voltaris Donnerklinge",
            "Electra Blitzschlag",
            "Raiden Donnerfaust",
            "Jolt Elektrosturm",
            "Thor Donnergroll",

            "Terran Steinschmied",
            "Gaia Erdenwächter",
            "Atlas Felsenfaust",
            "Terra Erdenschild",
            "Golem Felsensturm",

            "Glacies Frostschild",
            "Frostius Eisessturm",
            "Tundrus Eisbann",
            "Chillara Frosthauch",
            "Blizzara Frostfeuer",

            "Zephyrus Windrufer",
            "Aeris Luftklinge",
            "Aeolus Sturmbote",
            "Galea Windesflügel",
            "Caelus Wolke"
    );
    private static final List<String> heroTypes = List.of(
            "Feuer", "Feuer", "Feuer", "Feuer", "Feuer",
            "Wasser", "Wasser", "Wasser", "Wasser", "Wasser",
            "Pflanze", "Pflanze", "Pflanze", "Pflanze", "Pflanze",
            "Elektro", "Elektro", "Elektro", "Elektro", "Elektro",
            "Erde", "Erde", "Erde", "Erde", "Erde",
            "Eis", "Eis", "Eis", "Eis", "Eis",
            "Luft", "Luft", "Luft", "Luft", "Luft"
    );
    private static final List<Integer> hpValues = List.of(
            12, 11, 14, 9, 13,
            11, 10, 13, 12, 9,
            10, 9, 12, 13, 11,
            9, 8, 11, 10, 14,
            15, 13, 10, 12, 11,
            10, 12, 14, 11, 9,
            9, 11, 13, 10, 12
    );



    private static final List<Integer> damageValues = List.of(
            12, 10, 8, 13, 14,
            11, 9, 14, 12, 13,
            10, 12, 9, 14, 13,
            13, 14, 11, 10, 8,
            9, 13, 8, 12, 14,
            12, 10, 14, 13, 9,
            14, 13, 10, 9, 12
    );


    private static final List<String> extras = List.of(
            "Entfesselt ein feuriges Inferno!",
            "Brennt mit der Hitze von tausend Sonnen!",
            "Schmiedet Schwerter aus flüssiger Lava!",
            "Tanzt um Feuerstürme herum!",
            "Herrscht über Vulkan und Flamme!",

            "Beherrscht die Wellen und Meerestiere.",
            "Beruhigt und kontrolliert die Gezeiten.",
            "Tiefseeabenteuer und Ozeansymphonie!",
            "Ruft die Meeresgötter zu Hilfe!",
            "Wasserwirbel und Strudelmeisterschaft!",

            "Umschlingt Gegner mit Ranken!",
            "Meister der Photosynthese und des Waldwachstums.",
            "Blütenzauber und Fruchtbarkeitsernte!",
            "Wurzeln, die bis in die Tiefen reichen!",
            "Beherrscht den Wald in seiner ganzen Pracht!",

            "Schlägt mit der Kraft eines Gewitters zu.",
            "Elektrisiert das Schlachtfeld mit einem Funken.",
            "Blitze zucken durch die Luft!",
            "Ladung, die die Luft zum Zerreißen bringt!",
            "Donnerhall und elektrische Eruption!",

            "Fest wie ein Berg, unnachgiebig.",
            "Mutter der Erde und der Natur.",
            "Erdbeben und Steinschatten!",
            "Felsen, die wie Wasser fließen!",
            "Wurzeln, die sich tief in die Erde graben!",

            "Eisige Kältepeitsche!",
            "Frostiger Sturm!",
            "Schneesturm und gefrorene Stille!",
            "Glitzerndes Eis und kristalline Eleganz!",
            "Frostbeulen und gefrorene Schicksale!",

            "Ruft die Winde und Stürme herbei!",
            "Tanzt mit den Lüften!",
            "Flügel aus Wolken und federleichter Tanz!",
            "Sturmfänger und Wolkenwanderer!",
            "Sphärenklänge und Windgesang!"
    );


    @PostConstruct
    public void init() {
        loadHeroes();
        loadAccounts();
        loadBackgrounds();
        loadCardDesigns();
    }

    @PreDestroy
    public void shutdown() {
        saveHeroes();
        saveAccounts();
        saveBackgrounds();
    }

    private void loadAccounts() {
        try {
            File file = new File(ACCOUNTS_FILE_PATH);
            if (file.exists()) {
                accounts = mapper.readValue(file, new TypeReference<>() {});
            } else {
                System.out.println("acc.json Datei existiert nicht.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveAccounts() {
        try {
            mapper.writeValue(new File(ACCOUNTS_FILE_PATH), accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        if (accounts.containsKey(username)) {
            Account existingAccount = accounts.get(username);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Benutzername existiert bereits",
                    "username", username,
                    "coins", existingAccount.coins()
            ));
        }

        Account newAccount = new Account(username, password, 0);
        accounts.put(username, newAccount);
        saveAccounts();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", username,
                "coins", 0
        ));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        Account account = accounts.get(username);
        if (account != null && account.password().equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", username,
                    "coins", account.coins()
            ));
        }

        return ResponseEntity.ok(Map.of("success", false));
    }

    @PostMapping("/getUserInfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Kein Benutzername angegeben"
            ));
        }

        Account account = accounts.get(username);
        if (account == null) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Benutzer nicht gefunden"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", account.username(),
                "password", account.password(),
                "coins", account.coins()
        ));
    }



    @PostMapping("/updateAccount")
    public ResponseEntity<Map<String, Object>> updateAccount(@RequestBody Map<String, String> updateData) {
        String oldUsername = updateData.get("oldUsername");
        String newUsername = updateData.get("newUsername");
        String newPassword = updateData.get("newPassword");

        Account account = accounts.get(oldUsername);
        if (account == null) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Benutzer nicht gefunden"
            ));
        }

        if (!oldUsername.equals(newUsername) && accounts.containsKey(newUsername)) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Neuer Benutzername bereits vergeben"
            ));
        }

        accounts.remove(oldUsername);
        Account updatedAccount = new Account(
                newUsername,
                newPassword != null ? newPassword : account.password(),
                account.coins()
        );
        accounts.put(newUsername, updatedAccount);
        saveAccounts();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Kontoinformationen erfolgreich aktualisiert"
        ));
    }

    @PostMapping("/updateCoins")
    public ResponseEntity<Map<String, Object>> updateCoins(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        int coins = (int) request.get("coins");

        Account account = accounts.get(username);
        if (account != null) {
            Account updatedAccount = new Account(account.username(), account.password(), coins);
            accounts.put(username, updatedAccount);
            saveAccounts();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "coins", updatedAccount.coins()
            ));
        }

        return ResponseEntity.ok(Map.of("success", false));
    }


    @PostMapping("/addCoins")
    public ResponseEntity<Map<String, Object>> addCoins(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        int amount = (int) request.get("amount");

        Account account = accounts.get(username);
        if (account != null) {
            Account updatedAccount = new Account(account.username(), account.password(), account.coins() + amount);
            accounts.put(username, updatedAccount);
            saveAccounts();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "coins", updatedAccount.coins()
            ));
        }

        return ResponseEntity.ok(Map.of("success", false));
    }

    @GetMapping("/hero-types")
    public List<String> getHeroTypes() {
        return heroTypes;
    }

    @PostMapping("/hero")
    public ResponseEntity<Hero> createHero(@RequestBody Hero hero) {
        if (!heroTypes.contains(hero.type())) {
            return ResponseEntity.badRequest().build();
        }
        else {
            Hero newHero = new Hero(counter.getAndIncrement(), hero.name(), hero.HP(), hero.Damage(), hero.type(), hero.extra());
            heroes.add(newHero);
            return ResponseEntity.ok(newHero);
        }
    }

    @GetMapping("/createProfiles")
    public String createProfiles() {
        counter.set(1);
        heroes.clear();

        for (int i = 0; i < heroNames.size(); i++) {
            String name = heroNames.get(i);
            String element = heroTypes.get(i);
            int hp = hpValues.get(i);
            int damage = damageValues.get(i);
            String description = extras.get(i);

            Hero hero = new Hero(
                    counter.getAndIncrement(),
                    name,
                    hp,
                    damage,
                    element,
                    description
            );
            heroes.add(hero);
        }

        return "Profile wurden erfolgreich erstellt!";
    }



    private void loadHeroes() {
        try {
            File file = new File(HEROES_FILE_PATH);
            if (file.exists()) {
                heroes = mapper.readValue(file, new TypeReference<>() {});
                if (!heroes.isEmpty()) {
                    counter.set(heroes.stream().mapToInt(Hero::id).max().getAsInt() + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHeroes() {
        try {
            mapper.writeValue(new File(HEROES_FILE_PATH), heroes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/heroedit")
    public String editHero(@RequestBody Hero heroe) {
        if (heroe.HP() < 0 || heroe.HP() > 100 || heroe.Damage() < 0 || heroe.Damage() > 100 || heroe.type() == null) {
            return "Fehlgeschlagen!";
        }

        if (heroe.id() >= 0) {
            for (int i = 0; i < heroes.size(); i++) {
                Hero hero = heroes.get(i);
                if (hero.id() == heroe.id()) {
                    heroes.set(i, heroe);
                    return "Erfolgreich!";
                }
            }
        }

        return "Fehlgeschlagen!";
    }

    @DeleteMapping("/herodelete")
    public String delhero(@RequestBody(required = false) Hero herodel, @RequestParam(value = "id", required = false) Integer id) {
        if (herodel != null && herodel.id() >= 0) {
            for (int i = 0; i < heroes.size(); i++) {
                Hero hero = heroes.get(i);
                if (hero.id() == herodel.id()) {
                    heroes.remove(i);
                    return "Passt!";
                }
            }
        } else if (id != null && id >= 0) {
            for (int i = 0; i < heroes.size(); i++) {
                Hero hero = heroes.get(i);
                if (hero.id() == id) {
                    heroes.remove(i);
                    return "Passt!";
                }
            }
        }
        return "Passt nicht!";
    }

    @GetMapping("/heroshow")
    public List<Hero> showHero() {
        return heroes;
    }

    @DeleteMapping("/delall")
    public String deleteAllHeroes() {
        heroes.clear();
        counter.set(1);
        return "Alle Helden wurden gelöscht!";
    }

    private final Map<String, String> backgrounds = new HashMap<>();
    private static final String BACKGROUNDS_FILE_PATH = "/app/data/back.json";


    @PostMapping("/hasBackground")
    public ResponseEntity<Map<String, Object>> hasBackground(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String background = request.get("background");

        if (username == null || background == null) {
            return ResponseEntity.badRequest().body(Map.of("hasBackground", false));
        }

        boolean hasBackground = backgrounds.containsKey(username);
        boolean isActive = hasBackground && backgrounds.get(username).equals(background);

        return ResponseEntity.ok(Map.of(
                "hasBackground", hasBackground,
                "isActive", isActive
        ));
    }

    @PostMapping("/checkUserBackground")
    public ResponseEntity<Map<String, Object>> checkUserBackground(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String background = backgrounds.get(username);
        boolean exists = background != null;

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        if (exists) {
            response.put("activeBackground", background);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/buyBackground")
    public ResponseEntity<Map<String, Object>> buyBackground(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String background = (String) request.get("background");
        int cost = (int) request.get("cost");

        Account account = accounts.get(username);
        if (account != null) {
            if (account.coins() >= cost) {
                Account updatedAccount = new Account(account.username(), account.password(), account.coins() - cost);
                accounts.put(username, updatedAccount);
                saveAccounts();

                backgrounds.put(username, background);
                saveBackgrounds();

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "coins", updatedAccount.coins(),
                        "background", background
                ));
            } else {
                return ResponseEntity.ok(Map.of("success", false, "message", "Nicht genug Münzen"));
            }
        }

        return ResponseEntity.ok(Map.of("success", false, "message", "Benutzer nicht gefunden"));
    }

    @PostMapping("/toggleBackground")
    public ResponseEntity<Map<String, Object>> toggleBackground(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String background = (String) request.get("background");
        if (backgrounds.containsKey(username)) {
            String currentBackground = backgrounds.get(username);
            boolean isActive = currentBackground != null && currentBackground.equals(background);
            if (isActive) {
                backgrounds.put(username, "");
            } else {
                backgrounds.put(username, background);
            }
            saveBackgrounds();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "isActive", !isActive
            ));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Benutzer hat keinen Hintergrund"));
        }
    }

    @PostMapping("/getBackground")
    public ResponseEntity<Map<String, Object>> getBackground(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");

        if (backgrounds.containsKey(username)) {
            String background = backgrounds.get(username);
            return ResponseEntity.ok(Map.of("success", true, "background", background));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Kein Hintergrund für diesen Benutzer gefunden"));
        }
    }

    private void loadBackgrounds() {
        try {
            File file = new File(BACKGROUNDS_FILE_PATH);
            if (file.exists()) {
                backgrounds.putAll(mapper.readValue(file, new TypeReference<Map<String, String>>() {}));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBackgrounds() {
        try {
            mapper.writeValue(new File(BACKGROUNDS_FILE_PATH), backgrounds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/checkUserCardDesign")
    public ResponseEntity<Map<String, Object>> checkUserCardDesign(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        boolean purchased = cardDesigns.containsKey(username);
        String activeDesign = cardDesigns.getOrDefault(username, "");

        return ResponseEntity.ok(Map.of(
                "purchased", purchased,
                "activeDesign", activeDesign
        ));
    }

    @PostMapping("/buyCardDesign")
    public ResponseEntity<Map<String, Object>> buyCardDesign(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        int cost = (int) request.get("cost");

        Account account = accounts.get(username);
        if (account != null) {
            if (account.coins() >= cost) {
                Account updatedAccount = new Account(account.username(), account.password(), account.coins() - cost);
                accounts.put(username, updatedAccount);
                saveAccounts();

                cardDesigns.put(username, "default");
                saveCardDesigns();

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "coins", updatedAccount.coins(),
                        "activeDesign", "default"
                ));
            } else {
                return ResponseEntity.ok(Map.of("success", false, "message", "Nicht genug Münzen"));
            }
        }

        return ResponseEntity.ok(Map.of("success", false, "message", "Benutzer nicht gefunden"));
    }

    @PostMapping("/toggleCardDesign")
    public ResponseEntity<Map<String, Object>> toggleCardDesign(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String designId = request.get("designId");

        if (cardDesigns.containsKey(username)) {
            cardDesigns.put(username, designId);
            saveCardDesigns();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "activeDesign", cardDesigns.get(username)
            ));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Benutzer hat keine Kartendesigns gekauft"));
        }
    }

    private void loadCardDesigns() {
        try {
            File file = new File(CARDS_FILE_PATH);
            if (file.exists()) {
                cardDesigns.putAll(mapper.readValue(file, new TypeReference<Map<String, String>>() {}));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCardDesigns() {
        try {
            mapper.writeValue(new File(CARDS_FILE_PATH), cardDesigns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
