/*
$("#test").html( $("#test").text() );

   $(document).ready(function() {
       var text = $("#test").val();
       text = text.replace('&lt;','<','&gt;','>','&quot;','"');
   });*/



   document.ReadURL.readFile("/js/file.txt");
    setTimeout("showFileContent()",100);

    function showFileContent() {
     if (document.ReadURL.finished==0) {
      setTimeout("showFileContent()",100);
      return;
     }
     fileContent=document.ReadURL.fileContent;
     document.test.value=fileContent;

    }