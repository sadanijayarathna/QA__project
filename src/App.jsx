import React, { useState } from 'react';
import Login from './Components/Login/Login';
import Signup from './Components/Signup/Signup';
import TaskManager from './Components/TaskManager/TaskManager';
import './App.css';

function App() {
  const [page, setPage] = useState('login');
  const [user, setUser] = useState(null);

  const handleLogin = (email) => {
    setUser(email);
    setPage('tasks');
  };

  const handleSignup = (email) => {
    setUser(email);
    setPage('tasks');
  };

  const handleLogout = () => {
    setUser(null);
    setPage('login');
  };

  return (
    <div className={`fade-in`}>
      {user ? (
        <>
          <button className="logout-btn" onClick={handleLogout}>Logout</button>
          <TaskManager />
        </>
      ) : page === 'login' ? (
        <Login onLogin={handleLogin} onSwitchToSignup={() => setPage('signup')} />
      ) : (
        <Signup onSignup={handleSignup} onSwitchToLogin={() => setPage('login')} />
      )}
    </div>
  );
}

export default App;
