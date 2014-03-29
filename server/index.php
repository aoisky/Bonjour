<?php
/**
 * index.php
 * 
 * A demo html UI. It does not do any sanity checking.
 *
 * @author	Xiangyu Bu
 * @date	Feb 28, 2014
 */

include "config.inc.php";
include "include/class.User.php";
?>
<h1>A demo html log in box</h1>

<h3>Log in</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="login" />
    <label>Username</label>
    <input class="login_input" type="text" name="username" required />

    <label>Password</label>
    <input id="login_input_password" class="login_input" type="password" name="password" autocomplete="off" required />

    <input type="submit" />

</form>

<h3>Test token</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="test" />
    <label>Username</label>
    <input class="login_input" type="text" name="username" required />

    <label>Token</label>
    <input id="login_input_password" class="login_input" type="text" name="access_token" autocomplete="off" required />

    <input type="submit" />

</form>

<h3>Log out</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="logout" />
    <label>Username</label>
    <input class="login_input" type="text" name="username" required />

    <label>Token</label>
    <input id="login_input_password" class="login_input" type="text" name="access_token" autocomplete="off" required />

    <input type="submit" />

</form>

<h3>Change password</h3>
<form method="post" action="action.php" name="rstform">
	<input type="hidden" name="action" value="change_password" />
    <label for="rst_input_username">Username</label>
    <input id="rst_input_username" class="rst_input" type="text" name="username" required />
	
	<label for="rst_token">Token</label>
    <input id="rst_token" class="login_input" type="text" name="access_token" autocomplete="off" required />
	
    <label for="rst_input_password">Password</label>
    <input id="rst_input_password" class="rst_input" type="password" name="oldpassword" autocomplete="off" required />
	
	<label for="rst_new_password">New Password</label>
    <input id="rst_new_password" class="rst_input" type="password" name="newpassword" autocomplete="off" required />
	
	<label for="rst_re_password">Password</label>
    <input id="rst_re_password" class="rst_input" type="password" name="retype" autocomplete="off" required />
	
    <input type="submit" />

</form>

<h3>Register</h3>
<form method="post" action="action.php" name="registerform">
	<input type="hidden" name="action" value="register" />
	<input type="hidden" name="avatar" value="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" />
    <!-- the user name input field uses a HTML5 pattern check -->
    <p><label>Username (only letters, numbers, and &quot;_&quot; 4 to 32 characters)</label>
    <input class="login_input" type="text" pattern="[a-zA-Z0-9_]{4,32}" name="username" required />
	</p>
    <!-- the email input field uses a HTML5 email type check -->
    <p>
	<label for="login_input_email">User's email</label>
    <input id="login_input_email" class="login_input" type="email" name="email" required />
	</p>
	<p>
    <label for="login_input_password_new">Password (min. 6 characters)</label>
    <input id="login_input_password_new" class="login_input" type="password" name="password" pattern=".{6,}" required autocomplete="off" />
	</p>
    <p>
	<label for="login_input_password_repeat">Repeat password</label>
    <input id="login_input_password_repeat" class="login_input" type="password" name="retype" pattern=".{6,}" required autocomplete="off" />
    </p>
	<p>
	<input type="submit"  name="register" value="Register" />
	</p>
</form>

<h3>Get Security Questions</h3>
<form method="post" action="action.php" name="registerform">
	<input type="hidden" name="action" value="getSecurityQuestions" />
	<p>
	<input type="submit" />
	</p>
</form>

<h3>Forgot Password</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="forgotPassword" />
    <label>Username</label>
    <input class="login_input" type="text" name="username" required />
    <input type="submit" />
</form>

<h3>Verify Security Answer</h3>
<form method="post" action="action.php" name="vform">
	<input type="hidden" name="action" value="verifySecurityAnswer" />
    <label>Username</label>
    <input class="login_input" type="text" name="username" required />

    <label>Answer</label>
    <input id="login_input_password" class="login_input" type="text" name="answer" autocomplete="off" required />

    <input type="submit" />
</form>

<h3>Get Profile</h3>
<form method="post" action="action.php" name="vqform">
	<input type="hidden" name="action" value="getProfile" />
    <label>Username</label>
    <input class="login_input" type="text" name="targetUser" required />
    <input type="submit" />
</form>

<h3>Update Profile</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="updateProfile" />
    <label for="up_input_username">Username</label>
    <input id="up_input_username" class="login_input" type="text" name="username" required />
	<br />
    <label>Token</label>
    <input class="login_input" type="text" name="access_token" autocomplete="off" required />
	<br />
	<?php
	$profile_fields_array = User::$DEFAULT_USER_PROFILE;
	foreach ($profile_fields_array as $key => $val){
		print "<label for=\"up_input_" . $key . "\">" . $key . "</label>\n";
		print "<input id=\"up_input_" . $key . "\" class=\"login_input\" type=\"text\" name=\"" . $key . "\" value=\"".$val."\" /><br />\n";	
	}
	?>
	
    <input type="submit" />

</form>

<script>
function base64_encode(data) {
  //  discuss at: http://phpjs.org/functions/base64_encode/
  // original by: Tyler Akins (http://rumkin.com)
  // improved by: Bayron Guevara
  // improved by: Thunder.m
  // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // improved by: Rafał Kukawski (http://kukawski.pl)
  // bugfixed by: Pellentesque Malesuada
  //   example 1: base64_encode('Kevin van Zonneveld');
  //   returns 1: 'S2V2aW4gdmFuIFpvbm5ldmVsZA=='
  //   example 2: base64_encode('a');
  //   returns 2: 'YQ=='
  //   example 3: base64_encode('✓ à la mode');
  //   returns 3: '4pyTIMOgIGxhIG1vZGU='

  var b64 = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  var o1, o2, o3, h1, h2, h3, h4, bits, i = 0,
    ac = 0,
    enc = '',
    tmp_arr = [];

  if (!data) {
    return data;
  }

  data = unescape(encodeURIComponent(data))

  do {
    // pack three octets into four hexets
    o1 = data.charCodeAt(i++);
    o2 = data.charCodeAt(i++);
    o3 = data.charCodeAt(i++);

    bits = o1 << 16 | o2 << 8 | o3;

    h1 = bits >> 18 & 0x3f;
    h2 = bits >> 12 & 0x3f;
    h3 = bits >> 6 & 0x3f;
    h4 = bits & 0x3f;

    // use hexets to index into b64, and append result to encoded string
    tmp_arr[ac++] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4);
  } while (i < data.length);

  enc = tmp_arr.join('');

  var r = data.length % 3;

  return (r ? enc.slice(0, r - 3) : enc) + '==='.slice(r || 3);
}

function $(name){
	return document.getElementsByName(name);
}

function v(name){
	var dom = document.getElementById(name);
	if (dom != null)
		return document.getElementById(name).value;
	else return -1;
}

function submitForm(){
	var matchCriteria = base64_encode("{" + "\"desiredRange\": \""+v("desiredRange")+"\", \"mLatitude\": \""+v("mLatitude")+"\",  \"mLongitude\": \""+v("mLongitude")+"\",  \"mAltitude\": \""+v("mAltitude")+"\"}");
	document.getElementById("mData").value = matchCriteria;
	document.getElementById("matchingf").submit();
}
</script>

<h3>Match</h3>
<form method="post" action="action.php" name="loginform" id="matchingf">
	<input type="hidden" name="action" value="match" />
	<input type="hidden" id="mData" name="data" value="" />
	<label>Username</label>
    <input class="login_input" type="text" name="username" required />
	<br />
    <label>Token</label>
    <input id="login_input_password" class="login_input" type="text" name="access_token" autocomplete="off" required />
	<br />
    <label>desiredDistance</label>
    <input class="login_input" type="text"  id="desiredRange" name="desiredRange" value="10" required />
	<br />
    <label>mLatitude</label>
    <input class="login_input" type="text" id="mLatitude" name="mLatitude" autocomplete="off" value="15" required />
	<br />
	<label>mLongitude</label>
    <input class="login_input" type="text" id="mLongitude" name="mLongitude" autocomplete="off" value="20" required />
	<br />
	<label>mAltitude</label>
    <input class="login_input" type="text" id="mAltitude" name="mAltitude" autocomplete="off" value="25" required />
	<br />
    <input type="button" onclick="submitForm();" value="submit" />

</form>