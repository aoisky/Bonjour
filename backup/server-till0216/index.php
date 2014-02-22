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