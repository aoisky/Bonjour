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

?>
<h1>A demo html log in box</h1>

<h3>Log in</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="login" />
    <label for="login_input_username">Username</label>
    <input id="login_input_username" class="login_input" type="text" name="username" required />

    <label for="login_input_password">Password</label>
    <input id="login_input_password" class="login_input" type="password" name="password" autocomplete="off" required />

    <input type="submit" />

</form>

<h3>Test token</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="test" />
    <label for="login_input_username">Username</label>
    <input id="login_input_username" class="login_input" type="text" name="username" required />

    <label for="login_input_password">Token</label>
    <input id="login_input_password" class="login_input" type="text" name="access_token" autocomplete="off" required />

    <input type="submit" />

</form>

<h3>Log out</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="logout" />
    <label for="login_input_username">Username</label>
    <input id="login_input_username" class="login_input" type="text" name="username" required />

    <label for="login_input_password">Token</label>
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
    <p><label for="login_input_username">Username (only letters, numbers, and &quot;_&quot; 4 to 32 characters)</label>
    <input id="login_input_username" class="login_input" type="text" pattern="[a-zA-Z0-9_]{4,32}" name="username" required />
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
    <label for="login_input_username">Username</label>
    <input id="login_input_username" class="login_input" type="text" name="username" required />
    <input type="submit" />
</form>

<h3>Verify Security Answer</h3>
<form method="post" action="action.php" name="vform">
	<input type="hidden" name="action" value="verifySecurityAnswer" />
    <label for="login_input_username">Username</label>
    <input id="login_input_username" class="login_input" type="text" name="username" required />

    <label for="login_input_password">Answer</label>
    <input id="login_input_password" class="login_input" type="text" name="answer" autocomplete="off" required />

    <input type="submit" />
</form>

<h3>Get Profile</h3>
<form method="post" action="action.php" name="vqform">
	<input type="hidden" name="action" value="getProfile" />
    <label for="login_input_username">Username</label>
    <input id="login_input_username" class="login_input" type="text" name="targetUser" required />
    <input type="submit" />
</form>

<h3>Update Profile</h3>
<form method="post" action="action.php" name="loginform">
	<input type="hidden" name="action" value="updateProfile" />
    <label for="up_input_username">Username</label>
    <input id="up_input_username" class="login_input" type="text" name="username" required />
	<br />
    <label for="up_input_password">Token</label>
    <input id="up_input_password" class="login_input" type="text" name="access_token" autocomplete="off" required />
	<br />
	<?php
	$profile_fields_array = explode("|", $profile_fields);
	foreach ($profile_fields_array as $key){
		print "<label for=\"up_input_" . $key . "\">" . $key . "</label>\n";
		print "<input id=\"up_input_" . $key . "\" class=\"login_input\" type=\"text\" name=\"" . $key . "\" autocomplete=\"off\" /><br />\n";	
	}
	?>
	
    <input type="submit" />

</form>