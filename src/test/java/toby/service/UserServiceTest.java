package toby.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import toby.dao.UserDao;
import toby.domain.Level;
import toby.domain.User;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static toby.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static toby.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "/test-applicationContext.xml")
public class UserServiceTest {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserDao userDao;

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    List<User> users;	// test fixture

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkLevelUpgraded(updated.get(0), "joytouch", Level.SILVER);
        checkLevelUpgraded(updated.get(1), "madnite1", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        public void send(SimpleMailMessage[] mailMessage) throws MailException {
        }
    }

    private void checkLevelUpgraded(User updated, String expecedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expecedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);	  // GOLD 레벨
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

//    @Test
//    public void upgradeAllOrNothing() {
//        UserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
//        testUserService.setUserDao(this.userDao);
//        testUserService.setMailSender(this.mailSender);
//
//        UserServiceTx txUserService = new UserServiceTx();
//        txUserService.setTransactionManager(transactionManager);
//        txUserService.setUserService(testUserService);
//
//        userDao.deleteAll();
//        for(User user : users) userDao.add(user);
//
//        try {
//            testUserService.upgradeLevels();
//            fail("TestUserServiceException expected");
//        }
//        catch(TestUserServiceException e) {
//        }
//    }

    @Test
    public void upgradeAllOrNothing() {
        UserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(this.mailSender);

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");
        UserService txUserService = (UserService) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] {UserService.class}, txHandler
        );

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }
        catch(TestUserServiceException e) {
        }
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUsers() {
            return users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        @Override
        public void update(User user) {
            // Mock오브젝트 제공
            updated.add(user);
        }

        @Override
        public List<User> getAll() {
            // 스텁기능 제공
            return this.users;
        }


        // 테스트에 사용 안되는 메서드들
        @Override
        public void add(User user) { throw new UnsupportedOperationException(); }
        @Override
        public User get(String id) { throw new UnsupportedOperationException(); }
        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }
        @Override
        public int getCount() { throw new UnsupportedOperationException(); }


    }


    static class TestUserServiceImpl extends UserServiceImpl {
        private String id;

        private TestUserServiceImpl(String id) {
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }

}
