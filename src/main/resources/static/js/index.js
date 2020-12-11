$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	$("#hintModal").modal("show");
	// 獲取title 和 content
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(CONTEXT_PATH + "/discuss/add",
		{"title": title, "content": content},
		function (data) {
			data = $.parseJSON(data);
			// 在提示框中顯示返回信息
			$("#hintBody").text(data.msg);
			// 顯示提示框
			$("#hintBody").modal("show");
			// 2秒後, 自動隱藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新頁面
				if (data.code === 200) {
					window.location.reload();
				}
			}, 2000);
		}

	);


}