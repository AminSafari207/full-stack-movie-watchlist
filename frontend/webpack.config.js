const path = require('path')
const dotenv = require('dotenv')
const webpack = require('webpack')
const TerserJSPlugin = require('terser-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
// const CopyWebpackPlugin = require('copy-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
// const PWAManifestPlugin = require('webpack-pwa-manifest')
const stylusAutoPrefixer = require('autoprefixer-stylus')
const ESLintPlugin = require('eslint-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
// const { WebpackManifestPlugin } = require('webpack-manifest-plugin')
// const { InjectManifest } = require('workbox-webpack-plugin')
// const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')
// const { customChunkNames } = require('./webpack/data/chunkNames')
// const { SetChunkNamePlugin, generateEscapedPathMatchRegExp } = require('./webpack/plugins')
const { generateBanner, generateEnvKeys } = require('./webpack/utils')

const ENV = dotenv.config().parsed
const PKG = require('./package.json')
const BANNER = generateBanner('./banner.txt', {
  PKG,
  currentYear: new Date().getFullYear(),
})

module.exports = (env, argv) => {
  const isLive = argv.hot
  const isTest = process.env.IS_TEST === 'true'
  const isProduction = argv.mode === 'production'

  const NODE_ENV = argv.mode
  const PROTOCOL = ENV.REACT_APP_PROTOCOL
  const HOSTNAME = ENV.REACT_APP_HOSTNAME
  const ROOT_DIR = ENV.REACT_APP_ROOT_DIR
  const OUTPUT_DIR = ENV.REACT_APP_OUTPUT_DIR
  const PUBLIC_PATH = `/${ROOT_DIR}/${OUTPUT_DIR}`
  const BASE_URL = `${PROTOCOL}://${HOSTNAME}`
  const APP_URL = `${BASE_URL}/${PUBLIC_PATH}`

  const LOCAL_PATH = './'
  const LOCAL_URL = (path.resolve(__dirname, 'src') + '/').replace(/\\/g, '/')

  // const expressMockHost = ENV.REACT_APP_EXPRESS_MOCK_HOST
  // const expressMockPort = ENV.REACT_APP_EXPRESS_MOCK_PORT
  // const mockApiUrl = `http://${expressMockHost}:${expressMockPort}`

  const envKeys = generateEnvKeys({
    ...ENV,
    NODE_ENV,
    REACT_APP_PUBLIC_URL: isTest ? LOCAL_PATH : PUBLIC_PATH,
    REACT_APP_BASE_URL: BASE_URL,
    REACT_APP_URL: APP_URL,
    REACT_APP_LOCAL_URL: LOCAL_URL,
    REACT_APP_IS_TEST: isTest,
    REACT_APP_IS_LIVE: isLive,
  })

  return {
    mode: NODE_ENV,
    entry: {
      main: './src/Main.jsx',
    },
    output: {
      path: path.resolve(__dirname, OUTPUT_DIR),
      publicPath: isLive ? '' : isTest ? LOCAL_PATH : PUBLIC_PATH,
      filename: '[name].[contenthash].js',
      clean: true,
    },
    devServer: {
      static: {
        directory: path.join(__dirname, 'live'),
      },
      hot: true,
      compress: true,
      historyApiFallback: true,
      host: ENV.HOST || undefined,
      port: Number(ENV.PORT) || undefined,
      // client: {
      //   webSocketURL: ENV.WEBPACK_WS_SERVER || undefined,
      // },
      // proxy: [
      //   {
      //     context: ['/api'],
      //     target: mockApiUrl,
      //   },
      // ],
    },
    optimization: {
      minimize: isProduction,
      minimizer: [new TerserJSPlugin({})],
      runtimeChunk: true,
    },
    resolve: {
      alias: {
        src: path.resolve(__dirname, 'src'),
        'process/browser': require.resolve('process/browser'),
      },
      extensions: ['.js', '.jsx', '.json', '.css', './styl'],
      fallback: {
        buffer: require.resolve('buffer/'),
        process: require.resolve('process/browser'),
      },
    },
    module: {
      rules: [
        {
          test: /\.jsx?$/,
          exclude: /(node_modules)/,
          use: {
            loader: 'babel-loader',
            options: {
              presets: ['@babel/preset-env', ['@babel/preset-react', { runtime: 'automatic' }]],
            },
          },
        },
        {
          test: /.css$/,
          exclude: /(node_modules)/,
          use: ['style-loader', 'css-loader'],
        },
        {
          test: /.styl$/,
          exclude: /(node_modules)/,
          use: [
            isProduction ? 'style-loader' : MiniCssExtractPlugin.loader,
            'css-loader',
            {
              loader: 'stylus-loader',
              options: {
                stylusOptions: {
                  use: [stylusAutoPrefixer()],
                  compress: isProduction,
                },
              },
            },
          ],
        },
        {
          test: /\.(woff|woff2|eot|ttf|otf)$/i,
          exclude: /(node_modules)/,
          type: 'asset',
          generator: {
            filename: 'assets/fonts/[name][ext][query]',
          },
        },
        {
          test: /\.(png|jpe?g|gif|svg)$/i,
          exclude: /(node_modules)/,
          type: 'asset/resource',
          generator: {
            filename: 'assets/images/[name][ext]',
          },
        },
      ],
    },
    plugins: [
      new CleanWebpackPlugin(),
      // new ESLintPlugin({
      //   extensions: ['js', 'jsx'],
      //   emitWarning: true,
      //   emitError: true,
      //   failOnWarning: false,
      //   failOnError: false,
      // }),
      new webpack.DefinePlugin(envKeys),
      new webpack.HotModuleReplacementPlugin(),
      new webpack.ProvidePlugin({
        Buffer: ['buffer', 'Buffer'],
        process: 'process/browser',
      }),
      new webpack.BannerPlugin({
        banner: BANNER,
        raw: true,
      }),
      //   new PWAManifestPlugin({
      //     inject: true,
      //     filename: 'manifest.json',
      //     publicPath: '.',
      //     fingerprints: false,
      //     name: 'MondoTalk Portal',
      //     short_name: 'MondoTalk Portal',
      //     display: 'standalone',
      //     theme_color: '#ffffff',
      //     background_color: '#ffffff',
      //     start_url: APP_URL,
      //     icons: [
      //       {
      //         src: '',
      //         destination: 'assets/images',
      //         sizes: [96, 128, 192, 256, 384, 512],
      //       },
      //     ],
      //   }),
      new MiniCssExtractPlugin({
        filename: '[name].[contenthash].css',
      }),
      new HtmlWebpackPlugin({
        inject: true,
        template: './public/index.html',
        filename: 'index.html',
      }),
      //   new CopyWebpackPlugin({
      //     patterns: [
      //       {
      //         from: './public/silent-check-sso.html',
      //         to: 'silent-check-sso.html',
      //       },
      //       { from: './src/Assets/Images', to: 'Assets/Images' },
      //       { from: './src/Assets/Fonts', to: 'Assets/Fonts' },
      //     ],
      //   }),
      //   new InjectManifest({
      //     swSrc: path.resolve(
      //       __dirname,
      //       "src/Services/ServiceWorker/ServiceWorker.js"
      //     ),
      //     swDest: "sw.js",
      //     exclude: [
      //       /\.(?:ttf|woff|woff2)$/,
      //       /\.(?:png|jpg|jpeg|svg|webp|ico)$/,
      //       /\.css$/,
      //       /\.js$/,
      //       /\.json$/,
      //     ],
      //   }),
      // new BundleAnalyzerPlugin(),
      // new WebpackManifestPlugin({
      //   fileName: "assetManifest.json",
      //   generate: (seed, files, entrypoints) => {
      //     const manifestFiles = files.reduce((manifest, file) => {
      //       manifest[file.name] = file.path;
      //       return manifest;
      //     }, seed);
      //     const entrypointFiles = entrypoints.main.filter(
      //       (fileName) => !fileName.endsWith(".map")
      //     );

      //     return {
      //       files: manifestFiles,
      //       entrypoints: entrypointFiles,
      //     };
      //   },
      // }),
      // new SetChunkNamePlugin({
      //   match: generateEscapedPathMatchRegExp(customChunkNames),
      // }),
      //   new BundleAnalyzerPlugin({
      //     analyzerMode: "static", // The report outputs to an HTML file in the output directory
      //     reportFilename: "bundle-report.html", // Name of the report file
      //     openAnalyzer: true, // Automatically open the report in your default browser
      //   }),
    ],
    stats: {
      chunks: true, // Show list of chunks
      chunkModules: true, // Show modules within each chunk
      chunkOrigins: true, // Show origins of chunks
      colors: false, // Add colors to the output
      modules: false, // Do not show list of modules (except within chunks)
      reasons: true, // Do not show module reasons
      errorDetails: false, // Do not show error details
      entrypoints: false, // Do not show entry points
      hash: false, // Do not show the hash
      version: false, // Do not show the version
      timings: false, // Do not show build timings
      builtAt: false, // Do not show build date and time
      assets: false, // Do not show assets information
      children: false, // Do not show child compilations
      performance: false, // Do not show performance hints
      logging: 'warn', // Show warnings and errors only
      excludeModules: /node_modules/, // Exclude node_modules from the stats output
    },
  }
}

//######################################################
