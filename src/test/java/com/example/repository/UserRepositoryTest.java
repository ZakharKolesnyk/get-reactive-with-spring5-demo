package com.example.repository;

import com.example.domain.User;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.jdbc.JdbcTestUtils;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "issue", "mention", "message", "user");
    }

    @Test
    @DatabaseSetup(value = "user-activity.xml")
    public void shouldFindExpectedMostActiveUser() {
        StepVerifier.create(userRepository.findAllOrderedByActivityDesc(PageRequest.of(0, 1)))
                .expectNextCount(1)
                .expectNext(User.of("53316dc47bfc1a000000000f", "oledok"))
                .expectComplete();
    }

    @Test
    @DatabaseSetup("user-popularity.xml")
    public void shouldFindExpectedMostPopularUser() {
        StepVerifier.create(userRepository.findAllOrderedByMentionDesc(PageRequest.of(0, 1)))
                .expectNextCount(1)
                .expectNext(User.of("53316dc47bfc1a000000000f", "oledok"))
                .expectComplete();
    }
}
