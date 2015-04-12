var rightsArray = new Array();


function addRight(e) {
    var id = $(e).attr("id");
    if (rightsArray.indexOf(id) === -1) {
        rightsArray.push(id);
        $("[id$='"+id+"Input"+"']").val(true);
        var addedRight = "addedright-" + id;
        $("#addedRights").append($("<div>")
                .attr("id", addedRight)
                .html($(e).html()).append(" ").append($("<i>").addClass("glyphicon glyphicon-remove font-brightred")).addClass("badge alert-info pointer spacing rights-item")
                .click({
                    "id": addedRight,
                    "rightId": id
                }, function(e) {
                    rightsArray.splice(rightsArray.indexOf(e.data.rightId),1);
                    $("#" + e.data.id).remove();
                    $("[id$='"+e.data.rightId+"Input"+"']").val(false);
                })).append(" ");
    }
}