/*
 * Original code from:
 * Mattias Fagerlund
 * http://lotsacode.wordpress.com/2010/03/05/singularization-pluralization-in-c/
 * Matt Grande
 * http://mattgrande.wordpress.com/2009/10/28/pluralization-helper-for-c/
 *
 * Converted Java by Mark Renouf
 */
package com.itrade.common.infrastructure.util.string;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides ability to transform english words between their singular to plural
 * forms.
 *
 * @author Mark Renouf
 */
public class Plurals {
	// @formatter:off
	private static final Set<String> unpluralizables = ImmutableSet.of(
			"equipment", "information", "rice", "money", "species", "series",
			"fish", "sheep", "deer", "executive");

	private static final List<Replacer> singularizations = ImmutableList.of(
			//replace("(.*)people$").with("$1person"),
			replace("houses$").with("house"),
			replace("oxen$").with("ox"),
			replace("children$").with("child"),
			replace("personnes$").with("person"),
			replace("feet$").with("foot"),
			replace("teeth$").with("tooth"),
			replace("geese$").with("goose"),
			replace("(.*)ives?$").with("$1ife"),
			replace("(.*)ves?$").with("$1f"),
			replace("(.*)men$").with("$1man"),
			replace("(.+[aeiou])ys$").with("$1y"),
			replace("(.+[^aeiou])ies$").with("$1y"),
			replace("(.+)zes$").with("$1"),
			replace("([m|l])ice$").with("$1ouse"),
			replace("matrices$").with("matrix"),
			replace("indices$").with("index"),
			replace("(.+[^aeiou])ices$").with("$1ice"),
			replace("(.*)ices$").with("$1ex"),
			replace("(octop|vir)i$").with("$1us"),
			replace("(.+(s|x|sh|ch))es$").with("$1"),
			replace("(.+)s$").with("$1")
	);

	private static final List<Replacer> pluralizations = ImmutableList.of(
			//replace("(.*)person$").with("$1people"),
			replace("house$").with("houses"),
			replace("ox$").with("oxen"),
			replace("child$").with("children"),
			replace("foot$").with("feet"),
			replace("tooth$").with("teeth"),
			replace("goose$").with("geese"),
			replace("(.*)fe?$").with("$1ves"),
			replace("(.*)man$").with("$1men"),
			replace("(.+[aeiou]y)$").with("$1s"),
			replace("(.+[^aeiou])y$").with("$1ies"),
			replace("(.+z)$").with("$1zes"),
			replace("([m|l])ouse$").with("$1ice"),
			replace("(.+)(e|i)x$").with("$1ices"),
			replace("(octop|vir)us$").with("$1i"),
			replace("(.+(s|x|sh|ch))$").with("$1es"),
			replace("(.+)").with("$1s")
	);
	// @formatter:on

	/**
	 * If possible, ensure the provided word is a singular word form.
	 *
	 * @return The singular form of the word, or the input if no
	 * rules apply.
	 */
	public static String singularize(String word) {
		if (unpluralizables.contains(word.toLowerCase())) {
			return word;
		}

		for (final Replacer singularization : singularizations) {
			Matcher m = singularization.matches(word);
			if (m.matches()) {
				return singularization.replace(m);
			}
		}

		return word;
	}

	/**
	 * If possible, ensure the provided word is a plural word form.
	 *
	 * @return The plural form of the word, or the input if no
	 * rules apply.
	 */
	public static String pluralize(String word) {
		if (unpluralizables.contains(word.toLowerCase())) {
			return word;
		}

		for (final Replacer pluralization : pluralizations) {
			Matcher m = pluralization.matches(word);
			if (m.matches()) {
				return pluralization.replace(m);
			}
		}

		return word;
	}

	/**
	 * A simple helper class with a Builder to provide a little syntactic sugar
	 */
	static class Replacer {
		Pattern pattern;
		String replacement;

		static class Builder {
			private final Pattern pattern;

			Builder(Pattern pattern) {
				this.pattern = pattern;
			}

			Replacer with(String replacement) {
				return new Replacer(pattern, replacement);
			}
		}

		private Replacer(Pattern pattern, String replacement) {
			this.pattern = pattern;
			this.replacement = replacement;
		}

		Matcher matches(String word) {
			return pattern.matcher(word);
		}

		String replace(Matcher m) {
			return m.replaceFirst(replacement);
		}
	}

	static Replacer.Builder replace(String pattern) {
		return new Replacer.Builder(Pattern.compile(pattern));
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newCachedThreadPool();
		Collection<Callable<String>> tasks = Lists.newArrayList();
		for (int i = 0; i < 1000; i++) {
			tasks.add(() -> Plurals.singularize("rooms"));
			tasks.add(() -> Plurals.singularize("children"));
		}

		List<Future<String>> result = executor.invokeAll(tasks);
		int i = 0;
		for (Future<String> stringFuture : result) {
			System.out.println(stringFuture.get() + i);
			i++;
		}
		executor.shutdown();
	}

}
