
function constraintSelect(event, el, tableName, constraint){
  $('#modal-constaint-key').modal();
  
  $('#modal-constaint-key .modal-body').load("/api/constraint?constraint=" + constraint + "&table=" + tableName,
      function(responseTxt, statusTxt, xhr){

        if (responseTxt.includes("Project Name")) {
          $('#modal-constaint-key .modal-title').html("");
          $("#modal-constaint-key .modal-dialog").removeClass("modal-xl");
          $("#modal-constaint-key .modal-dialog").addClass("modal-lg");
          $("#modal-constaint-key .modal-header").remove();
          $("#modal-constaint-key .modal-dialog").css("width", '700px');

        
          $("#modal-constaint-key .modal-footer").remove();
        }else {
          $('#modal-constaint-key .modal-title').html(tableName + " 테이블의 "  + constraint + " 제약 조건");
          $("#modal-constaint-key .modal-dialog").removeClass("modal-lg");
          $("#modal-constaint-key .modal-dialog").addClass("modal-xl");

        }
        $('#modal-constaint-key').modal();
      } 
    );
}