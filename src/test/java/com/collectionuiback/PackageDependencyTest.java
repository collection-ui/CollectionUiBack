package com.collectionuiback;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses
public class PackageDependencyTest {

    public static final String ROOT_PACKAGE = "com.collectionuiback";

    public static final String PACKAGE_BOILERPLATE = ROOT_PACKAGE + ".boilerplate..";
    public static final String PACKAGE_MODULE = ROOT_PACKAGE + ".module..";
    public static final String PACKAGE_INFRA = ROOT_PACKAGE + ".infra..";
    public static final String PACKAGE_ACCOUNT = ROOT_PACKAGE + ".module.account..";
    public static final String PACKAGE_EXCEPTION = ROOT_PACKAGE + ".module.exception..";
    public static final String PACKAGE_OAUTH = ROOT_PACKAGE + ".module.oauth..";

    public static JavaClasses JAVA_CLASSES;

    @BeforeAll
    static void beforeAll() {
        JAVA_CLASSES = new ClassFileImporter()
                .importPackages(ROOT_PACKAGE);
    }

    @DisplayName("Boilerplate 패키지는 Module 패키지와 자기 자신에서만 참조가 가능하다.")
    @Test
    void boilerPlatePackageDependency() {
        ArchRule boilerPlatePackageDependency = classes()
                .that().resideInAPackage(PACKAGE_BOILERPLATE)
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(PACKAGE_MODULE, PACKAGE_BOILERPLATE);

        boilerPlatePackageDependency.check(JAVA_CLASSES);
    }

    @DisplayName("Boilerplate 패키지는 Infra, Module 패키지를 참조할 수 없다.")
    @Test
    void boilerPlatePackageDependency2() {
        ArchRule boilerPlatePackageDependency = noClasses()
                .that().resideInAPackage(PACKAGE_BOILERPLATE)
                .should().accessClassesThat().resideInAnyPackage(PACKAGE_INFRA, PACKAGE_MODULE);

        boilerPlatePackageDependency.check(JAVA_CLASSES);
    }

    @DisplayName("Infra 패키지는 Module 패키지와 자기 자신에서만 참조가 가능하다.")
    @Test
    void infraPackageDependency() {
        ArchRule infraPackageDependency = classes()
                .that().resideInAPackage(PACKAGE_INFRA)
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(PACKAGE_MODULE, PACKAGE_INFRA);

        infraPackageDependency.check(JAVA_CLASSES);
    }

    @DisplayName("Infra 패키지는 Boilerplate, Module 패키지를 참조할 수 없다.")
    @Test
    void infraPackageDependency2() {
        ArchRule infraPackageDependency = noClasses()
                .that().resideInAPackage(PACKAGE_INFRA)
                .should().accessClassesThat().resideInAnyPackage(PACKAGE_MODULE, PACKAGE_BOILERPLATE);

        infraPackageDependency.check(JAVA_CLASSES);
    }

    @DisplayName("Module 패키지내의 순환참조는 발생하지 않는다.")
    @Test
    void modulePackageCycleCheck() {
        ArchRule modulePackageCycleCheck = slices().matching("com.collectionuiback.module.(*)..")
                .should().beFreeOfCycles();

        modulePackageCycleCheck.check(JAVA_CLASSES);
    }

    @DisplayName("Exception 패키지는 Boilerplate 패키지만을 참조한다.")
    @Test
    void exceptionPackageDependency() {
        ArchRule exceptionPackageDependency = classes()
                .that().resideInAPackage(PACKAGE_EXCEPTION)
                .should().dependOnClassesThat().resideInAPackage(PACKAGE_BOILERPLATE);

        exceptionPackageDependency.check(JAVA_CLASSES);
    }

    @DisplayName("OAuth 패키지는 Boilerplate, Exception, Account 패키지만을 참조한다.")
    @Test
    void oauthPackageDependency() {
        ArchRule oauthPackageDependency = classes()
                .that().resideInAPackage(PACKAGE_OAUTH)
                .should().dependOnClassesThat().resideInAnyPackage(
                        PACKAGE_BOILERPLATE,
                        PACKAGE_EXCEPTION,
                        PACKAGE_ACCOUNT,
                        "java..",
                        "org.springframework.."
                );

        oauthPackageDependency.check(JAVA_CLASSES);
    }
}
