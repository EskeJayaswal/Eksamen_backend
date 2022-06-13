package facades;

import dtos.RentalDTO;
import entities.House;
import entities.User;
import errorhandling.NotFoundException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import entities.Rental;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class RentalFacadeTest {

    private static EntityManagerFactory emf;
    private static RentalFacade facade;

    public RentalFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = RentalFacade.getFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        ResetDB.truncate(emf);
        EntityManager em = emf.createEntityManager();

        User u1 = new User("jayas", "utavej", "bager", "jayas123","test123");
        User u2 = new User("laust", "filevej", "designer","last123","test123");

        Rental r1 = new Rental("01/01/2020", "01/01/2022", 200000, 30000,"Lone");
        Rental r2 = new Rental("06/06/2020", "06/06/2022", 20000, 5000,"Jakob");

        House h1 = new House("Birkevej", "kbh","4");
        House h2 = new House("Lodevej", "kbh","7");

        r1.addUser(u1);
        r2.addUser(u2);

        h1.addRental(r1);
        h1.addRental(r2);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Rental.deleteAllRows").executeUpdate();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();

            em.persist(u1);
            em.persist(u2);
            em.persist(r1);
            em.persist(r2);
            em.persist(h1);
            em.persist(h2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void create() throws Exception {
        Rental expected = new Rental("01/01/2021","01/01/2016",120000,10000,"Leif");
        Rental actual = facade.create(expected);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    void delete() throws NotFoundException {
        Rental rental = new Rental("01/01/2021","01/01/2016",120000,10000,"Leif");
        facade.create(rental);
        assertEquals(3, facade.getCount());
        facade.delete(1L);
        assertEquals(2, facade.getCount());
    }

    @Test
    public void getById() throws NotFoundException {
        // TypedQuery<House> query = em.createQuery("SELECT z FROM House z", House.class);

        Rental actual = facade.getById(1L);
        assertEquals(1L,actual.getId());

    }

    @Test
    public void getAll() {
        int expected = 2;
        int actual = facade.getAll().size();
        assertEquals(expected,actual);
    }

    @Test
    public void getUsersByRentalId() throws NotFoundException {
        List<User> users = facade.getUsersByRentalId(1L);

        assertEquals(1, users.get(0).getId());
       // assertEquals("01/01/2020", rentalList.get(0).getStartDate());

    }

   @Test
    public void getHouseByRentalId() throws NotFoundException {
        assertEquals("Birkevej",facade.getHouseByRentalId(1L).getAddress());

    }


}
