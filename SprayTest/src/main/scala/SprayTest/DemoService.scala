package Spray1


import java.io.{FileWriter, File}
import org.parboiled.common.FileUtils
import scala.concurrent.duration._
import akka.actor.{Props, Actor}
import akka.pattern.ask
import scala.concurrent.Future
import SprayTest.{MyJsonProtocol, Person}
import scala.collection.parallel.mutable
import scala.annotation.{Annotation, StaticAnnotation}
import scalax.io._
import SprayTest.Person

import org.scalatra._
import org.scalatra.scalate.ScalateSupport
import org.scalatra.swagger._
import org.scalatra.json.{ JValueResult, JacksonJsonSupport }
import org.scalatra.swagger._
import org.json4s._
import org.json4s.JsonDSL._


class FileProcessingApi (implicit val swagger: Swagger) extends ScalatraServlet
  with SwaggerSupport
  with ScalateSupport
  with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  override protected val applicationName: Option[String] = Some("file")
  protected val applicationName: Option[String] = Some("pet")
  protected val applicationDescription: String = "File Processing Api"

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }



   get("") {
     <html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl" >
       <body>
         <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
         <p>Defined resources:</p>
         <ul>
           <li><a href="/plik">/plik</a></li>
           <li><a href="/stats">/stats</a></li>
           <li><a href="/timeout">/timeout</a></li>
           <li><a href="/crash">/crash</a></li>
           <li><a href="/fail">/fail</a></li>
           <li><a href="/stop?method=post">/stop</a></li>
         </ul>
       </body>
     </html>
    }
   get("/plik") {
     <html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl" >
       <body>
         <h1>Say hello to <i>spray-can</i>!</h1>
         <p>Defined operations:</p>
         <ul>
           <li><a href="/plik/open">/Display file</a></li>
           <li><a href="/plik/addingName">/Add record</a></li>
           <li><a href="/plik/findBy">/Find by</a></li>
           <li><a href="/plik/edit">/Edit record</a></li>
           <li><a href="/plik/removeName">/Remove record</a></li>
         </ul>
       </body>
     </html>
    }
    get("/plik/open") {
      val source = scala.io.Source.fromFile("file.txt")
      val lines = source.mkString
      source.close()
      println(lines)
    }
    get("/plik/addingName") {
      <html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl" >
        <head>
          <link rel="stylesheet" type="text/css" href="/mystyle.css" ></link>
        </head>
        <body>
          <h1>Add to file</h1>
          <form name="input" action="/plik/append" method="post">
            <div id ="formWrapper">
              <label for="firstname">First name</label>
              <input type ="text" placeholder="First name" name="firstname"></input>
              <br/>

              <label for="age">Age</label>
              <input type ="text" placeholder="Age" name="age" ></input>
              <br/>

              <label for="sex">Sex</label>
              <input type ="text" placeholder="Male" name="sex" ></input>
              <br/>

              <label for="address">Address</label>
              <input type ="text" placeholder="Address" name="address" ></input>
              <br/>

              <input type="submit" value="Submit"></input>

              <br/>

            </div>
          </form>
        </body>
      </html>
    }
    get("/plik/removeName"){
      <html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl" >
        <body>
          <h1>Remove from file</h1>
          <form name="input" action="/plik/remove" method="delete" />
          Username: <input type="text" name="user" />
          <input type="submit" value="Submit" />
        </body>
      </html>
    }
    get("/plik/findBy"){
      <html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl" >
        <body>
          <h1>Find by </h1>
          <form name="input" action="/plik/find" method="get" />
          Name: <input type="text" name="name" /> <br/>
          Age: <input type="text" name="age" /> <br/>
          Sex: <input type="text" name="sex" /> <br/>
          Address: <input type="text" name="address" /> <br/>
          <input type="submit" value="Find" />
          <br/>
        </body>
      </html>
    }
    get("/plik/edit"){
      <html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl" >
        <body>
          <h1>Find record you want to edit </h1>
          <form name="input" action="/plik/edite" method="post" />
          Name: <input type="text" name="name" />
          New Name: <input type="text" name="newName" /> <br/>
          Age: <input type="text" name="age" />
          New Age: <input type="text" name="newAge" /><br/>
          Sex: <input type="text" name="sex" />
          New Sex: <input type="text" name="newSex" /><br/>
          Address: <input type="text" name="address" />
          New Address: <input type="text" name="newAddress" /><br/>
          <input type="submit" value="Find" />
          <br/>
        </body>
      </html>
    }
    get("/plik/find"){
          var name = multiParams.get("name")
          var age = multiParams.get("age")
          var sex = multiParams.get("sex")
          var address = multiParams.get("address")

                  var temp = 0
                  if (age.isEmpty ){
                    temp = -1
                  }
                  else  { temp = age.toInt }
                  var person = Person(name,temp,sex,address)
                  var result = ""
                  try{
                    var source = scala.io.Source.fromFile("file.txt")
                    for ( line <- source.getLines()){
                      var currentLineResult = findMatch( line, person)
                      result = result + currentLineResult
                      currentLineResult =""
                    }

                    source.close()
                  }
          println(result)
    }

    get("/remove"){
            var user = params.get("user")
                  val nameToRemove = user
                  val file: Seekable =  Resource.fromFile(new File("file.txt"))
                  var position = 0
                  try{
                    for ( line <- file.lines()){
                      if (parse(line).convertTo[Person].name.equals(nameToRemove)){
                        file.patch(position, "", OverwriteSome(line.length))
                        println(line)
                      }  else {
                        position = position + line.length
                      }
                    }
                  }
                  val source = scala.io.Source.fromFile("file.txt")
                  val lines = source.mkString
                  source.close()
                <p>{lines.toString}</p>
                }

      post("/stop")  {

           in(1.second){ actorSystem.shutdown()
           <div> Shutting down in 1 second...</div>
        }

      post("/plik/append"){
        var name = multiParams.get("firstname")
        var age = multiParams.get("age")
        var sex = multiParams.get("sex")
        var address = multiParams.get("address")

        val gender = sex.toLowerCase
        if( !(gender.equals("male") | gender.equals("female")) | firstname.isEmpty | age.isEmpty | address.isEmpty )
              complete(BadRequest, "Bad parameters used.")
        else{
             var personAge = 0
             try {
               personAge =age.toInt
             } catch {
               case ex: NumberFormatException =>{
                 personAge = -1
               }
             }
             if(personAge < 0 )
               complete(BadRequest, "Age must be a number higher or equal 0")
             else{
               val person  = Person(firstname , personAge, gender, address)
               val PersonFormat = jsonFormat(Person, "name", "age", "sex", "address")
               val file: Seekable =  Resource.fromFile("file.txt")
               file.append("\n" + PersonFormat.write(person))
               val source = scala.io.Source.fromFile("file.txt")
               val lines = source.mkString
               source.close()
               complete(lines)
             }
           }
         }

       post("/plik/edite"){

        var name = multiParams.get("name")
        var newName = multiParams.get("newName")
        var age = multiParams.get("age")
        var newAge = multiParams.get("newAge")
        var sex = multiParams.get("sex")
        var newSex = multiParams.get("newSex")
        var address = multiParams.get("address")
        var newAddress = multiParams.get("newAdress")

        var temp = 0
        var personAge = 0
        if (age.isEmpty ){
           temp = -1
          }
        else  {
           try {
                personAge =age.toInt
               } catch {
                   case ex: NumberFormatException =>{
                      personAge = -1
                  }
                 }
               }
        val gender = sex.toLowerCase
        val newGender = newSex.toLowerCase
        if(personAge < 0 | !(gender.equals("male") | gender.equals("female") | gender.isEmpty) | !(newGender.equals("male") | newGender.equals("female") | newGender.isEmpty))
             if (personAge < 0)
               complete(BadRequest, "Age must be a number higher or equal 0")
             else
               complete(BadRequest, "Wrong sex parameter use \"male\" or \"female\"")
             else{
                  if (temp == 0){
                    temp = age.toInt
                 }
        var temporary = 0
        val file: Seekable =  Resource.fromFile("file.txt")
        var position = 0
        val personToEdit = Person(name, temp, sex, address)
        try{
            val fileLenght = file.lines().mkString.length
            var offset = 0
            for( line <- file.lines()){
               var currentLineResult = ""
               println(line)
               if ( position + line.length <= fileLenght){
                   currentLineResult = findMatch( line, personToEdit)}
               else{
                    val linesubstring = line.substring(0, (line.length - offset))
                    if ( linesubstring.isEmpty == false)
                         currentLineResult = findMatch ( line.substring(0, (line.length-offset)), personToEdit )}
                    if ( currentLineResult.isEmpty){
                        position = position + line.length + 1
                      }
                      else {

                        if (newAge.isEmpty ){
                          temporary = -1
                        }
                        else  {
                          try {
                            personAge =newAge.toInt
                          } catch {
                            case ex: NumberFormatException =>{
                              personAge = -1
                            }
                          }
                        }
                        if (personAge < 0 ){
                          complete(BadRequest, "new Age parameter must be a number higher or equal 0")
                        } else {
                          if (temporary == 0){
                            temporary = newAge.toInt
                          }
                          val newLine =  editPerson(line , Person(newName, temporary, newSex, newAddress))
                          file.patch(position , newLine , OverwriteSome(line.length))
                          file.string
                          if ( newLine.length > line.length)
                            offset = offset + (newLine.length - line.length)
                          position = position + newLine.length + 1
                        }
                      }
                    }
                    val source = scala.io.Source.fromFile("file.txt")
                    val lines = source.mkString
                    source.close()
                    complete(lines)
                  }
                }
     }


}



  def in[U](duration: FiniteDuration)(body: => U): Unit =
    actorSystem.scheduler.scheduleOnce(duration)(body)

  def findMatch(line : String ,  personToFind : Person ) : String = {
//    import MyJsonProtocol._
    var currentLine =  parse(line).convertTo[Person]
    var currentLineResult =  line + "\n"

    //var string =""
    if (personToFind.name == "" ){
    }
    else if (currentLine.name != personToFind.name)
      currentLineResult =""
    if (personToFind.age ==  -1 ) {
    }
    else if(currentLine.age != personToFind.age)
      currentLineResult =""
    if (personToFind.sex == ""  ){
    }
    else if (currentLine.sex != personToFind.sex)
      currentLineResult = ""
    if (personToFind.address == ""  ){
    }
    else if (currentLine.address != personToFind.address)
      currentLineResult =""

    currentLineResult

  }

  def editPerson(line : String ,  personToEdit : Person ) : String = {
    var currentLine =  parse(line).convertTo[Person]
    println(personToEdit)
    var name = personToEdit.name
    var age = personToEdit.age
    var sex = personToEdit.sex
    var address = personToEdit.address
    //var string =""
    println(personToEdit)
    if (personToEdit.name.isEmpty ){
       name = currentLine.name
    }
    if (personToEdit.age ==  -1 ) {
       age = currentLine.age
    }
    if (personToEdit.sex.isEmpty  ){
       sex = currentLine.sex
       println("Edit person" + currentLine)
    }
    if (personToEdit.address.isEmpty  ){
       address = currentLine.address
    }
    PersonFormat.write(Person(name, age, sex, address)).toString()
  }

}
