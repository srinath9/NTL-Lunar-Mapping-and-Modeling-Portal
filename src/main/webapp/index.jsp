<html>
<head>
<title>NASA LMMP Image Generate REST API</title>
<script type="text/javascript"
	src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript"
	src="//cdnjs.cloudflare.com/ajax/libs/json2/20130526/json2.min.js"></script>
<script type="text/javascript"
	src="//cdnjs.cloudflare.com/ajax/libs/jquery-dateFormat/1.0/jquery.dateFormat.min.js"></script>
</head>
<body>
	<h1>NASA LMMP Image Generation REST API</h1>

	<!-- .................................................................................................................. -->
	<!-- .................................................................................................................. -->
	<!-- ........GENERATE NEW MOSAIC....................................................................................... -->
	<h2>Generate a new mosaic</h2>

	<form id="create-form">
		<table>
			<tr>
				<td>Min Collection Date (e.g. 1981-02-16T10:22:18):</td>
				<td><input id="min-collection-date" /></td>
				<td>Max Collection Date (e.g. 1981-02-16T10:22:18):</td>
				<td><input id="max-collection-date" /></td>
			</tr>
			<tr>
				<td>Western-most Longitude (0 to 360):</td>
				<td><input id="longitude-min" /></td>
				<td>Eastern-most Longitude (0 to 360):</td>
				<td><input id="longitude-max" /></td>
			</tr>
			<tr>
				<td>Min Latitude (-90 to 90):</td>
				<td><input id="latitude-min" />
				<td>Max Latitude (-90 to 90):</td>
				<td><input id="latitude-max" />
			</tr>
			<tr>
				<td>Min Illumination:</td>
				<td><input id="illumination-min" />
				<td>Max Illumination:</td>
				<td><input id="illumination-max" />
			</tr>
			<tr>
				<td>Min Camera Angle (0.0000 to 179.910):</td>
				<td><input id="camera-angle-min" />
				<td>Max Camera Angle (0.0000 to 179.910):</td>
				<td><input id="camera-angle-max" />
			</tr>
			<tr>
				<td>File Format:</td>
				<td><select id="file-format">
						<option value="png">PNG</option>
						<option value="gtiff">GeoTIFF</option>
				</select></td>
				<td>Output Size:</td>
				<td><select id="output-size">
						<option value="100">100% x 100%</option>
						<option value="90">90% x 90%</option>
						<option value="80">80% x 80%</option>
						<option value="70">70% x 70%</option>
						<option value="60">60% x 60%</option>
						<option value="50">50% x 50%</option>
						<option value="40">40% x 40%</option>
						<option value="30">30% x 30%</option>
						<option value="20">20% x 20%</option>
						<option value="10">10% x 10%</option>
						<option value="5">5% x 5%</option>
				</select></td>
			</tr>
			<tr>
				<td>Product Type</td>
				<td><select id="product-types" disabled="disabled">
						<option>Loading...</option>
				</select></td>
				<td>Camera Spec</td>
				<td><select id="camera-specs" disabled="disabled">
						<option>Loading...</option>
				</select></td>
			</tr>
			<tr>
				<td colspan="4" align="right"><input id="generate-button"
					type="button" value="Generate" />
			</tr>
			<td colspan="4"><div id="create-status" /></td>
			</tr>
		</table>
	</form>

	<script type='text/javascript'>
	$( document ).ready(function() {
		$.get( "rest/generate/productTypes", function(data) {
		    $("#product-types").empty();
		    $("#product-types").removeAttr("disabled");
		    
		    $.each(data, function(index, value) {
		      $("#product-types").append($("<option/>").attr("value", value).text(value));
		    });
		});
		
		$.get( "rest/generate/cameraSpecs", function(data) {
	    	$("#camera-specs").empty();
	    	$("#camera-specs").removeAttr("disabled");
	    
		    $.each(data, function(index, value) {
		      $("#camera-specs").append($("<option/>").attr("value", value).text(value));
		    });
		});		
	});
	
    function pushIfSet(request, key, id) {
      val = $(id).val();

      if (!val) {
        return;
      }

      request[key] = val;
    }

    function pushAsArrayIfSet(request, key, id) {
      val = $(id).val();

      if (!val) {
        return;
      }

      request[key] = $.map(val.split(','), $.trim);
    }

    /* attach a submit handler to the form */
    $("#generate-button").click(
        function(event) {
          /* stop form from submitting normally */
          event.preventDefault();

          var request = {};
          pushIfSet(request, "collectionDateMin", "#min-collection-date")
          pushIfSet(request, "collectionDateMax", "#max-collection-date")

          pushIfSet(request, "productType", "#product-type")
          pushIfSet(request, "cameraSpecification", "#camera-specification");
          pushIfSet(request, "longitudeMin", "#longitude-min");
          pushIfSet(request, "longitudeMax", "#longitude-max");
          pushIfSet(request, "latitudeMin", "#latitude-min");
          pushIfSet(request, "latitudeMax", "#latitude-max");
          pushIfSet(request, "illuminationMin", "#illumination-min");
          pushIfSet(request, "illuminationMax", "#illumination-max");
          pushIfSet(request, "cameraAngleMin", "#camera-angle-min");
          pushIfSet(request, "cameraAngleMax", "#camera-angle-max");
          pushIfSet(request, "outputFormat", "#file-format");
          pushIfSet(request, "outputSizePercentage", "#output-size");
          pushAsArrayIfSet(request, "targets", "#targets");

          $("#create-form :input").prop("disabled", true);

          $.ajax({
            type : "POST",
            url : "rest/generate",
            data : JSON.stringify(request),
            dataType : 'json',
            contentType : "application/json; charset=utf-8",
            success : function(result) {
              $("#create-form :input").prop("disabled", false);
              $("#create-form")[0].reset();
              $("#create-status").html(
                  "<h3>Created job with UUID " + result.trackingId + "</h3>");
            },
            error : function() {
              $("#create-form :input").prop("disabled", false);
              $("#create-status").html("<b>ERROR!</b> Check logs.");
            }
          });
        });
  </script>
	<!-- ........GENERATE NEW MOSAIC....................................................................................... -->
	<!-- .................................................................................................................. -->
	<!-- .................................................................................................................. -->


	<!-- .................................................................................................................. -->
	<!-- .................................................................................................................. -->
	<!-- ........CHECK JOB STATE........................................................................................... -->
	<h2>Check status of an existing job</h2>
	<table>
		<tr>
			<td>Job UUID:</td>
			<td><span id="job-uuid"></span></td>
			<td><div id="job-status-wrapper">
					<table>
						<tr>
							<td>Status:</td>
							<td><div id="job-status" /></td>
						</tr>
						<tr>
							<td>Reason:</td>
							<td><div id="job-reason" /></td>
						</tr>
						<tr>
							<td>Link:</td>
							<td><div id="job-link" /></td>
						</tr>
					</table>
				</div></td>
		</tr>
	</table>
	<!-- ........CHECK JOB STATE........................................................................................... -->
	<!-- .................................................................................................................. -->
	<!-- .................................................................................................................. -->



	<!-- .................................................................................................................. -->
	<!-- .................................................................................................................. -->
	<!-- ........REQUEST HISTORY........................................................................................... -->
	<h2>Request History</h2>
	<input id="request-history-button" type="button"
		value="Update Request History" />
	<div id="request-history-table" />

	<script type='text/javascript'>
    $("#request-history-button").click(
        function(event) {
          $("#request-history-button").prop("disabled", true);

          $.ajax({
            type : "GET",
            url : "rest/jobs",
            dataType : 'json',
            contentType : "application/json; charset=utf-8",
            success : function(resp) {
              $("#request-history-button").prop("disabled", false);
              
              var table = $('<table/>');
              table.prop("border", "1");
              
              var row = $('<tr/>');

              row.append($('<td/>'));
              row.append($('<td/>').text("uuid"));
              row.append($('<td/>').text("status"));
              row.append($('<td/>').text("requestDate"));
              row.append($('<td/>').text("requestEndDate"));
              row.append($('<td/>').text("numberOfImages"));
              row.append($('<td/>').text("imageUrls"));
              row.append($('<td/>').text("hadoopJobId"));
              row.append($('<td/>').text("failReason"));
              row.append($('<td/>').text("outputFormat"));
              row.append($('<td/>').text("outputSizePercentage"));
              row.append($('<td/>').text("collectionDateMin"));
              row.append($('<td/>').text("collectionDateMax"));
              row.append($('<td/>').text("productType"));
              row.append($('<td/>').text("cameraSpecification"));
              row.append($('<td/>').text("longitudeMin"));
              row.append($('<td/>').text("longitudeMax"));
              row.append($('<td/>').text("latitudeMin"));
              row.append($('<td/>').text("latitudeMax"));
              row.append($('<td/>').text("illuminationMin"));
              row.append($('<td/>').text("illuminationMax"));
              row.append($('<td/>').text("cameraAngleMin"));
              row.append($('<td/>').text("cameraAngleMax"));
              row.append($('<td/>').text("targets"));


              table.append(row);
              
              for (var i = 0; i < resp.length; i++) {
                row = $('<tr/>');

                row.append($('<td/>').append($('<button/>').attr('class', 'checkstate').text('Check')));
                row.append($('<td/>').text(resp[i].uuid));
                row.append($('<td/>').text(resp[i].status));
                row.append($('<td/>').text($.format.date(resp[i].requestStart)));
                row.append($('<td/>').text($.format.date(resp[i].requestEnd)));
                row.append($('<td/>').text(resp[i].numberOfImages));
                row.append($('<td/>').text(JSON.stringify(resp[i].imageUrls)));
                row.append($('<td/>').text(resp[i].hadoopJobId));
                row.append($('<td/>').text(resp[i].failReason));
                row.append($('<td/>').text(resp[i].outputFormat));
                row.append($('<td/>').text(resp[i].jobCriteria.outputSizePercentage));
                row.append($('<td/>').text($.format.date(resp[i].jobCriteria.collectionDateMin)));
                row.append($('<td/>').text($.format.date(resp[i].jobCriteria.collectionDateMax)));
                row.append($('<td/>').text(resp[i].jobCriteria.productType));
                row.append($('<td/>').text(resp[i].jobCriteria.cameraSpecification));
                row.append($('<td/>').text(resp[i].jobCriteria.longitudeMin));
                row.append($('<td/>').text(resp[i].jobCriteria.longitudeMax));
                row.append($('<td/>').text(resp[i].jobCriteria.latitudeMin));
                row.append($('<td/>').text(resp[i].jobCriteria.latitudeMax));
                row.append($('<td/>').text(resp[i].jobCriteria.illuminationMin));
                row.append($('<td/>').text(resp[i].jobCriteria.illuminationMax));
                row.append($('<td/>').text(resp[i].jobCriteria.cameraAngleMin));
                row.append($('<td/>').text(resp[i].jobCriteria.cameraAngleMax));
                row.append($('<td/>').text(JSON.stringify(resp[i].jobCriteria.targets)));
                table.append(row);
              }
              
              $('#request-history-table').html(table);
              
              $('.checkstate').click(function(event) {
                var button = $(this);
                var uuid = $(this).parent().next().text();
                
                $(button).attr('disabled', true);
                
                $('#job-uuid').text(uuid);
                $('#job-status-wrapper div').html('');

                $.ajax({
                  type : "GET",
                  url : "rest/status/" + uuid,
                  dataType : 'json',
                  contentType : "application/json; charset=utf-8",
                  success : function(resp) {
                    $(button).attr('disabled', false);
                    $('#job-status').text(resp.status);
                    $('#job-reason').text(resp.reason);

                    if (resp.link) {
                      $('#job-link').html(
                          "<a href='" + resp.link + "'>" + resp.link + "</a>");
                    }
                  },
                  error : function(result, textStatus, thrownError) {
                    $(button).attr('disabled', false);
                    resp = JSON.parse(result.responseText);
                    $('#job-status').text(resp.status);
                    $('#job-reason').text(resp.reason);
                  }
                });        
              })
            },
            error : function(result, textStatus, thrownError) {
              $("#request-history-button").prop("disabled", false);
              $("#request-history-table").html("<b>failed</b>");
            }
          });
        });
  </script>
	<!-- ........REQUEST HISTORY........................................................................................... -->
	<!-- .................................................................................................................. -->
	<!-- .................................................................................................................. -->
</body>
</html>
