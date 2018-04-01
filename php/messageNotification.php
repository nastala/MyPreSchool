<?php

	define( 'API_ACCESS_KEY', 'AAAAkkYFsIE:APA91bFka9rraIb59d0Xn3bYIDoRyN-xNT9zujZxh8nBFAWjeMcwmGGh2SRcrJ4JB0GhCsVhfo7hb733PFfuMQs8Efh0UQfcsNKGnjCQNz9TWmFyOb2_7cfV8WNe0eIkYKXreKAcY9U5' );
	$token = $_POST['token'];
	$message = $_POST['message'];
	$sender = $_POST['sender'];
	$key = $_POST['key'];
	
	$fcmMsg = array(
		'body' => $message,
		'title' => 'Your received a message from ' . $sender,
		'sound' => "default",
        'color' => "#203E78", 
		'key' => $key
	);
	
	$fcmFields = array(
		'to' => $token,
        'priority' => 'high',
		'notification' => $fcmMsg
	);
	
	$headers = array(
		'Authorization: key=' . API_ACCESS_KEY,
		'Content-Type: application/json'
	);
	
	$ch = curl_init();
	curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
	curl_setopt( $ch,CURLOPT_POST, true );
	curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
	curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
	curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
	curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fcmFields ) );
	$result = curl_exec($ch );
	curl_close( $ch );
	echo $result . "\n\n";
	
?>