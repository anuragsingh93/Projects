var id=null;
var msgid=0;
$(document).ready(function() {
  //  $("#submit").click(function()
    /*$.ajax({
        type: "POST",
        url: "/logging",
        data: formData,
        success: function(){},
        dataType: "json",
        contentType : "application/json"
    });*/
});
function  sub() {
    id = parseInt($("#id").val(), 10);
    var firstname = $("#fname").val();
    var lastname = $("#lname").val();
        var data = {
            id : id,
            firstname : firstname,
            lastname : lastname,
            creationepoch : $.now(),
            isactive : "y",
            lastmodified: $.now()
        }
        var formData = JSON.stringify(data);
        console.log(formData);
    $.ajax({
        type: "POST",
        cache: false,
        url: '/logging',
        data: formData,//.serialize(),
        contentType: 'application/json',
        dataType: "json",
        success: function(d) {
                alert("Submitted");
                update()
        },
        error: function (xhr, error) {
            //alert(error)
            console.log(xhr); console.log(error);
        }

    });

};

function update() {
    $.ajax({
        url: "/getusers",
        type: 'GET',
        data: {
            format: 'json'
        },
        success: function(response) {
               // console.log(response);
          //  response = $.parseJSON(response);
            //console.log(response)
            jQuery('#friendlist').html('');
            $(function() {
                $.each(response, function(i, item) {
                    $('<tr>').append(
                        $('<td>').text(item.firstname),
                        $('<td>').text(item.isActive)
                    ).appendTo('#friendlist');
                });
            });
        },
        error: function() {
                alert("failed")
        }
    });
    $.ajax({
        url: "/messages/"+msgid,
        type: 'GET',
        data: {
            format: 'json'
        },
        success: function(response) {
            //console.log(response);
            $(function() {
                var txt = $("textarea#tArea");
                var itemstr= txt.val();
                $.each(response, function(i, item) {
                    itemstr =itemstr+"\n"+item.content;
                    //console.log("Message Id:"+msgid+" "+item.msgid)
                    if(parseInt(item.msgid)>msgid){
                        msgid=parseInt(item.msgid);
                    }
                });
                txt.val(itemstr);
            });
        },
        error: function (xhr, error) {
            //alert(error)
            console.log(xhr); console.log(error);
        }
    });
    var lastchecked=$.now()
    $.ajax({
        url: "/notify/"+lastchecked,
        type: 'GET',
        data: {
            format: 'json'
        },
        success: function(response) {
            // console.log(response);
            //  response = $.parseJSON(response);
            //console.log(response)
            jQuery('#friendlist').html('');
            $(function() {
                $.each(response, function(i, item) {
                    $('<tr>').append(
                        $('<td>').text(item.firstname),
                        $('<td>').text(item.isActive)
                    ).appendTo('#friendlist');
                });
            });
        },
        error: function() {
            alert("failed")
        }
    });

        window.setTimeout(update, 5000);

}

function send() {
    var msgts = Math.round(new Date().getTime()/1000);
    var content = $("#msg").val();
    var data = {
        userid : id,
        msgts : msgts,
        content : content
    }
    var formData = JSON.stringify(data);
    console.log(formData);
    $.ajax({
        type: "POST",
        cache: false,
        url: '/insertchat',
        data: formData,//.serialize(),
        contentType: 'application/json',
        dataType: "json",
        success: function(d) {
            alert("Submitted");
            update()
        },
        error: function (xhr, error) {
            //alert(error)
            console.log(xhr); console.log(error);
        }

    });

};

function logout() {
    var data = {
        id : id,
        lastmodified: $.now()
    }
    var formData = JSON.stringify(data);
    console.log(formData);
    $.ajax({
        type: "PUT",
        cache: false,
        url: '/logout',
        data: formData,
        contentType: 'application/json',
        //dataType: "json",
        success: function(d) {
            alert("Logged Out");
            window.location="/login"
        },
        error: function (xhr, error) {
            //alert(error)
            console.log(xhr); console.log(error);
        }

    });

}