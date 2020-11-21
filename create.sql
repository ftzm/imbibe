DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE cocktails (
       id serial,
       name varchar(255),
       PRIMARY KEY (id)
);

CREATE TABLE ingredients (
       id serial,
       name varchar(255),
       PRIMARY KEY (id)
);

CREATE TABLE ingredient_use (
       id serial,
       cocktail_id integer,
       ingredient_id integer,
       CONSTRAINT fk_cocktails FOREIGN KEY(cocktail_id) REFERENCES cocktails(id),
       CONSTRAINT fk_ingredients FOREIGN KEY(ingredient_id) REFERENCES ingredients(id)
);

INSERT INTO cocktails (id, name)
VALUES
	(1, 'martini'),
	(2, 'sidecar'),
	(3, 'whiskey sour'),
	(4, 'last word');

INSERT INTO ingredients (id, name)
VALUES
	(1, 'gin'),
	(2, 'vermouth'),
	(3, 'lemon juice'),
	(4, 'sugar'),
	(5, 'brandy'),
	(6, 'cointreau'),
	(7, 'green chartreuse'),
	(8, 'bourbon'),
	(9, 'maraschino liquer'),
	(10, 'lime juice'),
	(11, 'simple syrup'),
	(12, 'angostura bitters');

INSERT INTO ingredient_use (cocktail_id, ingredient_id)
VALUES
        -- martini
	(1, 1),
	(1, 2),
	-- sidecar
	(2,5),
	(2,3),
	(2,6),
	-- whisky sour
	(3, 8),
	(3, 3),
	(3, 11),
	(3, 12),
	-- last word
	(4, 1),
	(4, 7),
	(4, 9),
	(4, 10);
