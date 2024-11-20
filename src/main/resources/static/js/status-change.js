$(function() {
	// class='status-change'が設定されたボタンをクリックしたらダイアログを表示する。
	$('.status-change').on('click', function() {
		let result = confirm('変更しますか');

		if (result) {
			return true;
		} else {
			return false;
		}
	});
});
