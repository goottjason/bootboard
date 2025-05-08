$(document).ready(function(){
  // 클릭시 tr 추가
  $(".addFileButton").on("click", function(e){

    let inputFileTag = `
      <tr>
        <td colspan="2">
          <input type="file" class="form-control" name="modifyNewFile" onchange="showPreview(this);"/>
        </td>
        <td>
          <input type="button" class="btn btn-danger btn-lg" value="파일첨부취소" onclick="cancelAddFile(this);"/>
        </td>
      </tr>`;

    let obj = $(e.target);
    let row = $(obj).parent().parent();
    $(inputFileTag).insertBefore(row);
  });

  $(".fileCheckbox").on("change", function(e){
    updateSelectedFileStatus();
  })
  $("#selectAll").on("change", function(e){
    let isChecked = $("#selectAll").is(":checked");
    $(".fileCheckbox").prop("checked", isChecked);
    updateSelectedFileStatus();
  })

  $(".removeUpFileBtn").on("click", function(e){
    let removeFileArr = [];
    $(".fileCheckbox").each(function(i, item){
      if($(item).is(":checked")){
        let tmp = $(item).data("file-id");
        removeFileArr.push(tmp);
      }
    });
    $.each(removeFileArr, function(i, item){
      console.log(item);
      $.ajax({
        url: "/board/modifyRemoveFileCheck",
        type: "POST",
        dataType: "json",
        data: {
          "removeFileNo" : item
        },
        success: function (data) {
          console.log(data);
          if(data.msg == "success") {
            $("input.fileCheckbox[data-file-id='" + item +"']").closest("tr").css("opacity", 0.2);
          }
        },
        error: function() {},
        complete: function() {}
      });
    });
  });

  $(".cancelRemove").on("click", function(e){
    $.ajax({
      url: "/board/cancelRemFiles",
      type: "POST",
      dataType: "json",
      success: function (data) {
        console.log(data);
        if(data.msg == "success") {
          $(".fileCheckbox").each(function(i, item){
            $(item).prop("checked", false);
            $(item).closest("tr").css("opacity", 1);
          })
        }
        $(".removeUpFileBtn").attr("disabled", true);
        updateSelectedFileStatus();
        $(".removeUpFileBtn").val("선택된 파일이 없습니다.");
      },
      error: function() {},
      complete: function() {}
    });
  })
});

function updateSelectedFileStatus() {
  let chkCount = $(".fileCheckbox:checked").length;
  if(chkCount > 0) {
    $(".removeUpFileBtn").removeAttr("disabled").val(chkCount +"개 파일 삭제");
  } else {
    $(".removeUpFileBtn").attr("disabled", true).val("선택된 파일이 없습니다.");
  }

  let total = $(".fileCheckbox").length;
  let checked = $(".fileCheckbox:checked").length;
  $("#selectAll").prop("checked", total > 0 && total === checked);
}

function showPreview(obj){
  console.log(obj.files);
  if(obj.files[0].size > 1*1024*1024){
    alert("<1MB이하의 파일만 업로드할 수 있습니다.>!");
    obj.value = ""; // 선택한 파일 초기화
    return;
  }
  // 파일 타입 확인
  let imageType = ["image/jpeg", "image/png", "image/gif", "image/jpg"];
  let fileType = obj.files[0].type;
  let fileName = obj.files[0].name;
  if(imageType.indexOf(fileType) != -1  ) {
    let reader = new FileReader();
    reader.readAsDataURL(obj.files[0]);
    // 파일 읽기를 완료하면 실행되는 콜백함수
    reader.onload = function(e){
      console.log(e.target);
      let imgTag = `
        <div style="">
          <img src="${e.target.result}" width="50px" class="img-thumbnail"></img>
          <span>${fileName}</span>
        </div>`;
      $(imgTag).insertAfter(obj);
    }
  } else {
    let imgTag = `
        <div style="">
          <img src="/images/noimage.png" width="50px" class="img-thumbnail"></img>
          <span>${fileName}</span>
        </div>`;
    $(imgTag).insertAfter(obj);
  }
}

function cancelAddFile(obj){
  // [파일첨부취소] 버튼을 클릭하면, 추가한 새 업로드 파일 미리보기 삭제
  let fileTag = $(obj).parent().prev().children().eq(0);
  $(fileTag).val(""); // 선택파일 초기화
  $(fileTag).parent().parent().remove(); // tr자체를 삭제
}

