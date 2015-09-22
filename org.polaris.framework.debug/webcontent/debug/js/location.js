$(function() {

	var currentPhone = null;

	$("#input_submit").click(function() {
		var phone = $("#input_phone").val();
		currentPhone = phone;
		load(phone);
		loadUserLocation(phone);
		$("#imageDiv1 div").empty().append("<img src='api/graphic/orignaggregation/" + phone + "' width='1200' height='600'>");
		$("#imageDiv2 div").empty().append("<img src='api/graphic/aggregation/" + phone + "' width='1200' height='600'>");
	});

	$("#userDiv a").click(function() {
		if (currentPhone == null) {
			return;
		}
		var userLocation = ajax.GET("/location/mining/" + currentPhone, null);
		updateUserLocation(userLocation);
	});

	function load(phone) {
		var $tbody = $("#resultDiv tbody");
		$tbody.empty();
		var locations = ajax.GET("/location/" + phone, null);
		for ( var i = 0; i < locations.length; i++) {
			var location = locations[i];
			var $tr = $("<tr></tr>");
			$tr.append($("<td></td>").text(i + 1));
			var loginDate = new Date(location.loginTime);
			var logoutDate = new Date(location.logoutTime);
			$tr.append($("<td></td>").text(loginDate.format("yyyy-MM-dd hh:mm:ss") + " - " + logoutDate.format("yyyy-MM-dd hh:mm:ss")));
			$tr.append($("<td></td>").text(location.lac));
			$tr.append($("<td></td>").text(location.ci));
			var state = location.state;
			var tdClass = "";
			if (state == -1) {
				tdClass = "unknown";
			} else if (state == 0) {
				tdClass = "normal";
			} else if (state == 1) {
				tdClass = "abnormal";
			} else if (state == 2) {
				tdClass = "guess";
			}
			if (location.state == -1) {
				// 无法定位坐标
				$tr.append($("<td class='" + tdClass + "'></td>").text("UnKnown"));
			} else {
				// 可以定位坐标
				var station = location.station;
				$tr.append($("<td class='" + tdClass + "'></td>").text("(" + station.lng + "," + station.lat + ")"));
			}
			$tbody.append($tr);
		}
	}

	function loadUserLocation(phone) {
		var userLocation = ajax.GET("/userlocation/" + phone, null);
		updateUserLocation(userLocation);
	}

	function updateUserLocation(userLocation) {
		var $tbody = $("#userDiv tbody");
		$tbody.empty();
		$("#userDiv .li_number span").text("");
		$("#userDiv .li_accuracy span").text("");
		$("#userDiv .li_noise span").text("");
		$("#userDiv .li_station span").text("");
		if (userLocation == null) {
			return;
		}
		$("#userDiv .li_number span").text(userLocation.number);
		$("#userDiv .li_accuracy span").text(formatNumber(userLocation.accuracy, "#,##0.00"));
		$("#userDiv .li_noise span").text(formatNumber(userLocation.noise, "#,##0.00"));
		$("#userDiv .li_station span").text(formatNumber(userLocation.station, "#,##0.00"));
		if (userLocation.number > 0) {
			var pArray = userLocation.locations;
			var tArray = userLocation.timeSegments;
			var eArray = userLocation.degrees;
			for ( var i = 0; i < userLocation.number; i++) {
				var $tr = $("<tr></tr>");
				$tr.append($("<td></td>").text(i + 1));
				$tr.append($("<td></td>").text(pArray[i]));
				$tr.append($("<td></td>").text(tArray[i]));
				$tr.append($("<td></td>").text(formatNumber(eArray[i], "#,##0.00")));
				$tbody.append($tr);
			}
		}
	}
});
