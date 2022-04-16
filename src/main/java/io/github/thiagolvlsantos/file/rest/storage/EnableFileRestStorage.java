package io.github.thiagolvlsantos.file.rest.storage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.github.thiagolvlsantos.file.rest.storage.EnableFileRestStorage.FileRestStorage;
import io.github.thiagolvlsantos.file.storage.EnableFileStorage;
import io.github.thiagolvlsantos.git.transactions.EnableGitTransactions;
import io.github.thiagolvlsantos.rest.storage.EnableRestStorage;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ FileRestStorage.class })
@EnableFileStorage
@EnableGitTransactions
@EnableRestStorage
public @interface EnableFileRestStorage {

	@Configuration
	@ComponentScan("io.github.thiagolvlsantos.file.rest.storage")
	public static class FileRestStorage {
	}
}