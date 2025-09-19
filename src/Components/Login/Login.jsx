import React, { useState } from 'react';
import './Login_new.css';

const Login = ({ onLogin, onSwitchToSignup }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [remember, setRemember] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please enter both email and password.');
      return;
    }
    
    setError('');
    setLoading(true);
    
    try {
      const response = await fetch('http://localhost:8080/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: email,
          password: password
        }),
      });
      
      const data = await response.json();
      
      if (response.ok) {
        // Store JWT token if needed
        if (data.accessToken) {
          localStorage.setItem('token', data.accessToken);
        }
        onLogin(email);
      } else {
        setError(data.message || 'Login failed. Please check your credentials.');
      }
    } catch (error) {
      setError('Network error. Please check if the server is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-split-bg">
      <div className="login-split-container">
        <div className="login-left">
          <div className="company-logo">🚀 TASK TRACKER</div>
          <h1>Welcome to...</h1>
          <p>Track your progress, monitor deadlines, and achieve your goals.<br/>Stay on top of your tasks with powerful tracking tools.</p>
          <div className="login-left-footer">✨ Your progress tracking companion</div>
        </div>
        <div className="login-right">
          <form className="login-form" onSubmit={handleSubmit}>
            <h2>Login</h2>
            <p className="login-desc">Welcome back! Login to track your tasks and monitor your progress.</p>
            <div className="input-group">
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="input-group">
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <div className="remember-row">
              <input
                type="checkbox"
                id="remember"
                checked={remember}
                onChange={() => setRemember(!remember)}
              />
              <label htmlFor="remember">Remember me</label>
            </div>
            {error && <div className="error">⚠️ {error}</div>}
            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? 'LOGGING IN...' : 'LOGIN'}
            </button>
            <div className="login-links">
              <span>New User? <button type="button" onClick={onSwitchToSignup} style={{background: 'none', border: 'none', color: '#007bff', textDecoration: 'underline', cursor: 'pointer'}}>Signup</button></span>
              <span className="forgot-link"><a href="#">Forgot your password?</a></span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
