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