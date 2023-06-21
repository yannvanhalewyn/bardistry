import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {hasError: false};
  }

  static getDerivedStateFromError(error) {
    return {hasError: true, error: error};
  }

  componentDidCatch(error, info) {
    // TODO submit to monitoring service
    console.log('ErrorBoundary.error', error);
    console.log('ErrorBoundary.info', info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <this.props.fallback
          onReset={() => {
            this.props.onReset();
            this.setState({hasError: false});
          }}
        />
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
