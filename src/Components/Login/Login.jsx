import React, { useState } from 'react';
import './Login_new.css';

const Login = ({ onLogin, onSwitchToSignup }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
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
          <div className="company-logo">🌊 FLOW HUB</div>
          <h1><span className="welcome-text">Welcome to</span><span className="main-text">...</span></h1>
          <p>Streamline your workflow, connect your ideas, and achieve seamless productivity.<br/>Navigate through your projects with intelligent flow management.</p>
          <div className="login-left-footer">✨ Your workflow optimization companion</div>
        </div>
        <div className="login-right">
          <form className="login-form" onSubmit={handleSubmit}>
            <h2>Login</h2>
            <p className="login-desc">Welcome back! Login to access your workflow hub and optimize your productivity.</p>
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
            {error && <div className="error">⚠️ {error}</div>}
            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? 'Logging In...' : 'Log In'}
            </button>
            <div className="signup-section">
              <span>New User? <button type="button" className="signup-btn" onClick={onSwitchToSignup}>Sign Up</button></span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
