package com.hparry.study;

import static com.hparry.study.tables.Author.AUTHOR;
import static com.hparry.study.tables.Book.BOOK;
import static com.hparry.study.tables.BookAuthorRel.BOOK_AUTHOR_REL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class BookStore {
	
	DatabaseConfiguration config;
	
	public BookStore(DatabaseConfiguration config) {
		this.config = config;
	}
	public void run() throws SQLException {
		Connection conn = getConnection();
		DSLContext create = DSL.using(conn, SQLDialect.POSTGRES_9_3);

		// query books for author named 'selena'
		Result<Record2<Long, String>> result = create.select(BOOK.ID, BOOK.TITLE).from(BOOK).join(BOOK_AUTHOR_REL)
				.on(BOOK_AUTHOR_REL.BOOKID.equal(BOOK.ID)).join(AUTHOR).on(BOOK_AUTHOR_REL.AUTHORID.equal(AUTHOR.ID))
				.where(AUTHOR.NAME.equal("selena")).orderBy(BOOK.TITLE.asc(), BOOK.ID.asc()).fetch();
		result.forEach((r) -> {
			System.out.println(String.format("%s (id: %s)", r.getValue(BOOK.TITLE), r.getValue(BOOK.ID)));
		});
		conn.close();
	}

	public static void main(final String[] args) throws SQLException {
		new BookStore().run();
	}

	private Connection getConnection() {
		try {
			Class.forName(config.driver).newInstance();
			return DriverManager.getConnection(config.url, config.user, config.password);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace(); // for tutorial's sake ;)
		}
		return null;
	}
}
