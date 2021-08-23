## Pre Requisites

In order to run this program you will need to first have Java 11 and Apache Maven installed on your machine

You can check for these requirements by using the commands:

mvn -v

java -version

If they are not already installed please go to [maven](https://maven.apache.org/install.html) and follow the instructions to setup Maven in order to run this program.

## Running The Program
In order to run the code you will need to download the zip file into whichever directory you prefer and UnZip it.

After this you simply need to cd into /RESTfulWebService and run the command 'mvn install -f pom.xml' which will build a JAR and run the Tests.

To execute the JAR itself you will need to run 'java -jar target/RESTfulWebService-0.0.1-SNAPSHOT.jar' command.

Additionally, to run the tests you can run 'mvn test -f pom.xml' command.

## Additional Information

####DB names are 'books' and 'patrons'

Call | _Description_
------------ | -------------
GET /book                                           | _gets all book_
GET /countBooks                                     | _counts all books (borrowed or available)_
GET /countBooksByName/{{title}}                     | _counts books by title (not exact title)_
GET /book/{{uuid}}                                  | _gets book by uuid_
GET /bookByExactTitle/{{title}}                     | _gets books by exact title_
GET /bookByTitle/{{title}}                          | _gets book by title (not exact title)_
GET /bookByISBN/{{isbn}}                            | _gets book by ISBN_
POST /book                                          | _add a new book_
PATCH /book                                         | _updates a book_
DELETE /bookById/{{uuid}}                           | _deletes a book by uuid_
GET /patron/{{uuid}}                                | _get Patron by uuid_
GET /booksBorrowedBy/{{uuid}}                       | _gets books borrowed by Patron uuid_
POST /patron                                        | _add a new patron_
PATCH /patron                                       | _updates a patron_
POST /patron/{{patron_uuid}}/borrow/{{book_uuid}}   | _borrows a book by book_uuid for patron by patron_uuid_
POST /patron/{{patron_uuid}}/return/{{book_uuid}}   | _returns a book by book_uuid for patron by patron_uuid_
DELETE /patron/{{uuid}}                             | _deletes a patron by uuid_
