// because it seems impossible in a set command to put a string literal in a scripted test
object Constants {
  val IncludeFast = "--include-categories=test.Fast"
  val IncludeSlow = "--include-categories=test.Slow"
  val IncludeFastAndSlow = "--include-categories=test.Fast,test.Slow"
  val ExcludeFast = "--exclude-categories=test.Fast"
  val ExcludeSlow = "--exclude-categories=test.Slow"
  val ExcludeFastAndSlow = "--exclude-categories=test.Fast,test.Slow"
}
