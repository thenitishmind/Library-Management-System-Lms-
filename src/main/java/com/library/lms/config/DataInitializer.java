package com.library.lms.config;

import com.library.lms.entity.*;
import com.library.lms.entity.enums.*;
import com.library.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Seeds the MySQL database with roles, permissions, fine policies,
 * default admin, sample members, and sample books on first startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final StaffRepository staffRepository;
    private final FinePolicyRepository finePolicyRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("DataInitializer: data already present, skipping seed.");
            return;
        }
        log.info("DataInitializer: seeding database...");

        // ── Roles ─────────────────────────────────────────────────────────────
        Role adminRole = roleRepository.save(Role.builder()
                .name("ROLE_ADMIN").description("System administrator").permissions(new HashSet<Permission>()).build());
        Role libRole = roleRepository.save(Role.builder()
                .name("ROLE_LIBRARIAN").description("Library staff").permissions(new HashSet<Permission>()).build());
        Role memberRole = roleRepository.save(Role.builder()
                .name("ROLE_MEMBER").description("Library member").permissions(new HashSet<Permission>()).build());

        // ── Fine policies ──────────────────────────────────────────────────────
        finePolicyRepository.saveAll(Arrays.asList(
            FinePolicy.builder().membershipType(MembershipType.STANDARD)
                .dailyRate(new BigDecimal("5.00")).maxFine(new BigDecimal("500.00"))
                .gracePeriodDays((short)1).maxLoanDays((short)14)
                .maxRenewals((short)2).maxActiveLoans((short)5).build(),
            FinePolicy.builder().membershipType(MembershipType.PREMIUM)
                .dailyRate(new BigDecimal("2.00")).maxFine(new BigDecimal("200.00"))
                .gracePeriodDays((short)3).maxLoanDays((short)21)
                .maxRenewals((short)3).maxActiveLoans((short)10).build(),
            FinePolicy.builder().membershipType(MembershipType.STUDENT)
                .dailyRate(new BigDecimal("1.00")).maxFine(new BigDecimal("100.00"))
                .gracePeriodDays((short)2).maxLoanDays((short)14)
                .maxRenewals((short)2).maxActiveLoans((short)5).build()
        ));

        // ── Admin staff account (admin@library.com / Admin@123) ───────────────
        staffRepository.save(Staff.builder()
            .employeeCode("EMP-0001")
            .email("admin@library.com")
            .firstName("System")
            .lastName("Admin")
            .passwordHash(passwordEncoder.encode("Admin@123"))
            .designation("Administrator")
            .role(adminRole)
            .isActive(true)
            .build());

        // ── Sample members ─────────────────────────────────────────────────────
        memberRepository.saveAll(Arrays.asList(
            Member.builder()
                .memberCode("MEM-0001").email("alice@example.com")
                .firstName("Alice").lastName("Johnson")
                .passwordHash(passwordEncoder.encode("Password@1"))
                .phone("9876543210").membershipType(MembershipType.PREMIUM)
                .membershipExpiry(LocalDate.now().plusYears(1))
                .status(MemberStatus.ACTIVE).role(memberRole).build(),
            Member.builder()
                .memberCode("MEM-0002").email("bob@example.com")
                .firstName("Bob").lastName("Smith")
                .passwordHash(passwordEncoder.encode("Password@1"))
                .phone("9123456780").membershipType(MembershipType.STUDENT)
                .membershipExpiry(LocalDate.now().plusYears(1))
                .status(MemberStatus.ACTIVE).role(memberRole).build(),
            Member.builder()
                .memberCode("MEM-0003").email("carol@example.com")
                .firstName("Carol").lastName("Williams")
                .passwordHash(passwordEncoder.encode("Password@1"))
                .phone("9001122334").membershipType(MembershipType.STANDARD)
                .membershipExpiry(LocalDate.now().plusMonths(6))
                .status(MemberStatus.ACTIVE).role(memberRole).build()
        ));

        // ── Sample categories ──────────────────────────────────────────────────
        Category fiction = categoryRepository.save(Category.builder()
                .name("Fiction").slug("fiction").description("Fictional literature").build());
        Category science = categoryRepository.save(Category.builder()
                .name("Science").slug("science").description("Science and technology").build());
        Category history = categoryRepository.save(Category.builder()
                .name("History").slug("history").description("Historical works").build());
        Category programming = categoryRepository.save(Category.builder()
                .name("Programming").slug("programming").description("Software development").build());
        Category selfHelp = categoryRepository.save(Category.builder()
                .name("Self-Help").slug("self-help").description("Personal development").build());

        // ── Sample authors ─────────────────────────────────────────────────────
        Author orwell  = authorRepository.save(Author.builder().firstName("George").lastName("Orwell").nationality("British").build());
        Author tolkien = authorRepository.save(Author.builder().firstName("J.R.R.").lastName("Tolkien").nationality("British").build());
        Author hawking = authorRepository.save(Author.builder().firstName("Stephen").lastName("Hawking").nationality("British").build());
        Author yuval   = authorRepository.save(Author.builder().firstName("Yuval").lastName("Harari").nationality("Israeli").build());
        Author martin  = authorRepository.save(Author.builder().firstName("Robert").lastName("Martin").nationality("American").build());
        Author covey   = authorRepository.save(Author.builder().firstName("Stephen").lastName("Covey").nationality("American").build());
        Author knuth   = authorRepository.save(Author.builder().firstName("Donald").lastName("Knuth").nationality("American").build());
        Author dahl    = authorRepository.save(Author.builder().firstName("Roald").lastName("Dahl").nationality("British").build());

        // ── Sample books ───────────────────────────────────────────────────────
        bookRepository.saveAll(Arrays.asList(
            Book.builder().isbn("978-0451524935").title("1984").language("en")
                .publicationYear(1949).totalPages(328).status(BookStatus.ACTIVE)
                .description("A dystopian social science fiction novel.")
                .authors(new HashSet<Author>(Arrays.asList(orwell)))
                .categories(new HashSet<Category>(Arrays.asList(fiction))).build(),
            Book.builder().isbn("978-0618640157").title("The Lord of the Rings").language("en")
                .publicationYear(1954).totalPages(1178).status(BookStatus.ACTIVE)
                .description("An epic high-fantasy novel by J.R.R. Tolkien.")
                .authors(new HashSet<Author>(Arrays.asList(tolkien)))
                .categories(new HashSet<Category>(Arrays.asList(fiction))).build(),
            Book.builder().isbn("978-0553380163").title("A Brief History of Time").language("en")
                .publicationYear(1988).totalPages(212).status(BookStatus.ACTIVE)
                .description("From the Big Bang to Black Holes.")
                .authors(new HashSet<Author>(Arrays.asList(hawking)))
                .categories(new HashSet<Category>(Arrays.asList(science))).build(),
            Book.builder().isbn("978-0062316097").title("Sapiens: A Brief History of Humankind").language("en")
                .publicationYear(2011).totalPages(443).status(BookStatus.ACTIVE)
                .description("A brief history of humankind from the Stone Age to the present.")
                .authors(new HashSet<Author>(Arrays.asList(yuval)))
                .categories(new HashSet<Category>(Arrays.asList(history))).build(),
            Book.builder().isbn("978-0132350884").title("Clean Code").language("en")
                .publicationYear(2008).totalPages(431).status(BookStatus.ACTIVE)
                .description("A Handbook of Agile Software Craftsmanship.")
                .authors(new HashSet<Author>(Arrays.asList(martin)))
                .categories(new HashSet<Category>(Arrays.asList(programming))).build(),
            Book.builder().isbn("978-0743269513").title("The 7 Habits of Highly Effective People").language("en")
                .publicationYear(1989).totalPages(381).status(BookStatus.ACTIVE)
                .description("Powerful lessons in personal change.")
                .authors(new HashSet<Author>(Arrays.asList(covey)))
                .categories(new HashSet<Category>(Arrays.asList(selfHelp))).build(),
            Book.builder().isbn("978-0201896831").title("The Art of Computer Programming Vol 1").language("en")
                .publicationYear(1968).totalPages(652).status(BookStatus.ACTIVE)
                .description("Fundamental Algorithms - the definitive work on algorithms.")
                .authors(new HashSet<Author>(Arrays.asList(knuth)))
                .categories(new HashSet<Category>(Arrays.asList(programming))).build(),
            Book.builder().isbn("978-0142410370").title("Charlie and the Chocolate Factory").language("en")
                .publicationYear(1964).totalPages(176).status(BookStatus.ACTIVE)
                .description("The classic tale of Willy Wonka and his magical factory.")
                .authors(new HashSet<Author>(Arrays.asList(dahl)))
                .categories(new HashSet<Category>(Arrays.asList(fiction))).build(),
            Book.builder().isbn("978-0553573404").title("Animal Farm").language("en")
                .publicationYear(1945).totalPages(112).status(BookStatus.ACTIVE)
                .description("A political allegory about power and corruption.")
                .authors(new HashSet<Author>(Arrays.asList(orwell)))
                .categories(new HashSet<Category>(Arrays.asList(fiction))).build()
        ));

        log.info("DataInitializer: seeded {} books, {} members. Login: admin@library.com / Admin@123",
                bookRepository.count(), memberRepository.count());
    }
}
