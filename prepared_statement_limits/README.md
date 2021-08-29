Where can you use `?` in a PreparedStatement?  

Consider these queries:
1. `CREATE DATABASE ?`
1. `DROP DATABASE ?`
1. `CREATE USER ? IDENTIFIED BY ?`
1. `DROP USER ?`
1. `CREATE TABLE ? (sample_str VARCHAR(256), sample_int INT)`
1. `CREATE TABLE sample_table (? VARCHAR(256), sample_int INT)`
1. `DROP TABLE ?`
1. `INSERT INTO ? (sample_str, sample_int) values ('sample_value', 1)`
1. `INSERT INTO sample_table (?, sample_int) values ('sample_value', 1)`
1. `INSERT INTO sample_table (sample_str, sample_int) values (?, 1)`
1. `DELETE FROM ?`
1. `DELETE FROM sample_table WHERE ?='sample_value'`
1. `DELETE FROM sample_table WHERE sample_str=?`
1. `SELECT * FROM ?`
1. `SELECT ? FROM sample_table`
1. `SELECT * FROM sample_table WHERE ?='sample_value'`
1. `SELECT * FROM sample_table WHERE sample_str=?`


<details>
<summary>Click to reveal the solution by experiment.</summary>
You cannot use ? as a reference (like a table name or column name).


Use can use ? where ever a value makes sense, even in `CREATE USER ?`
and `DROP USER ?`, since the username is a value.
```
✖          CREATE DATABASE ?
✖          DROP DATABASE ?
✔          CREATE USER ? IDENTIFIED BY ?
✔          DROP USER ?
✖          CREATE TABLE ? (sample_str VARCHAR(256), sample_int INT)
✖          CREATE TABLE sample_table (? VARCHAR(256), sample_int INT)
✖          DROP TABLE ?
✖          INSERT INTO ? (sample_str, sample_int) values ('sample_value', 1)
✖          INSERT INTO sample_table (?, sample_int) values ('sample_value', 1)
✔          INSERT INTO sample_table (sample_str, sample_int) values (?, 1)
✖          DELETE FROM ?
✖          DELETE FROM sample_table WHERE ?='sample_value'
✖          DELETE FROM sample_table WHERE sample_str=?
✖          SELECT * FROM ?
✖          SELECT ? FROM sample_table
✖          SELECT * FROM sample_table WHERE ?='sample_value'
✔          SELECT * FROM sample_table WHERE sample_str=?
```
</details>
