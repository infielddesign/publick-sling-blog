if($("#pageeditform").length!=0){
    $("select[name='primarytype']").on("change", function(){
        var typeValue = $(this).find(":selected").val()
        console.log(typeValue);

        if(typeValue==="sling:Folder"){
            $("#configurationName").addClass("hide");
            $("#visible").addClass("hide");
            $("#keywords").addClass("hide");
            $("#links").addClass("hide");
            $("#scripts").addClass("hide");
            $("#description").addClass("hide");
            $("#contentfield").addClass("hide");
        }
        if(typeValue==="publick:page"){
            $("#configurationName").removeClass("hide");
            $("#visible").removeClass("hide");
            $("#keywords").removeClass("hide");
            $("#links").removeClass("hide");
            $("#scripts").removeClass("hide");
            $("#description").removeClass("hide");
            $("#contentfield").removeClass("hide");
        }
    });
}