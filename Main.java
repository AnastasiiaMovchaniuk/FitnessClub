// ===============================
// FINAL PROJECT: Fitness Club
// Java 8 compatible
// ===============================

import java.util.*;

// ---------- exceptions ----------
class MembershipExpiredException extends RuntimeException {
    public MembershipExpiredException(String message) {
        super(message);
    }
}

// ---------- model ----------
abstract class Person {
    protected String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Client extends Person {
    private Membership membership;

    public Client(String name, Membership membership) {
        super(name);
        this.membership = membership;
    }

    public Membership getMembership() {
        return membership;
    }
}

class Trainer extends Person {
    public Trainer(String name) {
        super(name);
    }
}

// ---------- membership (abstraction + polymorphism) ----------
abstract class Membership {
    protected boolean active = true;

    public abstract double getPrice();

    public boolean isActive() {
        return active;
    }

    public void expire() {
        active = false;
    }
}

class MonthlyMembership extends Membership {
    public double getPrice() {
        return 500;
    }
}

class YearlyMembership extends Membership {
    public double getPrice() {
        return 5000;
    }
}

// ---------- factory (Factory Method) ----------
class MembershipFactory {
    public static Membership createMembership(String type) {
        if (type.equalsIgnoreCase("MONTH")) {
            return new MonthlyMembership();
        }
        if (type.equalsIgnoreCase("YEAR")) {
            return new YearlyMembership();
        }
        throw new IllegalArgumentException("Unknown membership type");
    }
}

// ---------- strategy ----------
interface PriceStrategy {
    double calculatePrice(Membership membership);
}

class DefaultPriceStrategy implements PriceStrategy {
    public double calculatePrice(Membership membership) {
        if (!membership.isActive()) {
            throw new MembershipExpiredException("Membership expired");
        }
        return membership.getPrice();
    }
}

// ---------- observer ----------
interface Observer {
    void update(String message);
}

class ClientObserver implements Observer {
    private Client client;

    public ClientObserver(Client client) {
        this.client = client;
    }

    public void update(String message) {
        System.out.println("Notification for " + client.getName() + ": " + message);
    }
}

// ---------- service (Singleton + Facade + GRASP Controller) ----------
class FitnessClub {
    private static FitnessClub instance;

    private List<Client> clients = new ArrayList<Client>();
    private List<Observer> observers = new ArrayList<Observer>();

    private FitnessClub() {}

    public static FitnessClub getInstance() {
        if (instance == null) {
            instance = new FitnessClub();
        }
        return instance;
    }

    public void addClient(Client client) {
        clients.add(client);
        notifyObservers("New client added");
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }
}

// ---------- application ----------
public class Main {
    public static void main(String[] args) {
        FitnessClub club = FitnessClub.getInstance();

        Membership membership = MembershipFactory.createMembership("MONTH");
        Client client = new Client("Ivan", membership);

        club.registerObserver(new ClientObserver(client));
        club.addClient(client);

        PriceStrategy strategy = new DefaultPriceStrategy();
        System.out.println("Membership price: " + strategy.calculatePrice(membership));
    }
}
