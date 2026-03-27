describe('Basic tests', () => {
  it('should pass sanity check', () => {
    expect(1 + 1).toBe(2);
  });

  it('should handle string', () => {
    expect('docker').toContain('dock');
  });
});
